/*
 * Copyright 2000-2010 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package git4idea.ui.branch;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.EditorBasedWidget;
import com.intellij.ui.popup.PopupFactoryImpl;
import com.intellij.ui.popup.list.ListPopupImpl;
import com.intellij.util.Consumer;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import git4idea.GitUtil;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.*;
import git4idea.config.GitVcsSettings;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import git4idea.validators.GitNewBranchNameValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.awt.event.MouseEvent;

/**
 * Status bar widget which displays actions for git flow
 * @author Kirill Likhodedov, Opher Vishnia
 */
public class GitFlowWidget extends EditorBasedWidget implements StatusBarWidget.MultipleTextValuesPresentation,
        StatusBarWidget.Multiframe,
        GitRepositoryChangeListener {
    private final GitVcsSettings mySettings;
    private volatile String myText = "";
    private volatile String myTooltip = "";
    private final String myMaxString;

    public GitFlowWidget(Project project) {
        super(project);
        project.getMessageBus().connect().subscribe(GitRepository.GIT_REPO_CHANGE, this);
        mySettings = GitVcsSettings.getInstance(project);
        myMaxString = "Git: Rebasing master";



    }

    @Override
    public StatusBarWidget copy() {
        return new GitBranchWidget(getProject());
    }

    @NotNull
    @Override
    public String ID() {
        return GitFlowWidget.class.getName();
    }

    @Override
    public WidgetPresentation getPresentation(@NotNull PlatformType type) {
        return this;
    }

    @Override
    public void selectionChanged(FileEditorManagerEvent event) {
        update();
    }

    @Override
    public void fileOpened(FileEditorManager source, VirtualFile file) {
        update();
    }

    @Override
    public void fileClosed(FileEditorManager source, VirtualFile file) {
        update();
    }

    @Override
    public void repositoryChanged(@NotNull GitRepository repository) {
        update();
    }

    @Override
    public ListPopup getPopupStep() {
        Project project = getProject();
        if (project == null) {
            return null;
        }
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);
        if (repo == null) {
            return null;
        }

        DefaultActionGroup popupGroup = new DefaultActionGroup(null, false);
        popupGroup.addAction(new StartFeatureAction(myProject));
        popupGroup.addAction(new FinishFeatureAction(myProject));
        ListPopup listPopup = new PopupFactoryImpl.ActionGroupPopup("Gitflow actions", popupGroup, SimpleDataContext.getProjectContext(project), false, false, false, true, null, -1,
                null, null);

        return listPopup;
    }

    @Override
    public String getSelectedValue() {
        return "Gitflow";
    }

    @NotNull
    @Override
    public String getMaxValue() {
        return myMaxString;
    }

    @Override
    public String getTooltipText() {
        return myTooltip;
    }

    @Override
    // Updates branch information on click
    public Consumer<MouseEvent> getClickConsumer() {
        return new Consumer<MouseEvent>() {
            public void consume(MouseEvent mouseEvent) {
                update();
            }
        };
    }

    private void update() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                Project project = getProject();
                if (project == null) {
                    emptyTextAndTooltip();
                    return;
                }

                GitRepository repo = GitBranchUtil.getCurrentRepository(project);
                if (repo == null) { // the file is not under version control => display nothing
                    emptyTextAndTooltip();
                    return;
                }

                int maxLength = myMaxString.length() - 1; // -1, because there are arrows indicating that it is a popup
                myText = StringUtil.shortenTextWithEllipsis(GitBranchUtil.getDisplayableBranchText(repo), maxLength, 5);
                myTooltip = getDisplayableBranchTooltip(repo);
                myStatusBar.updateWidget(ID());
                mySettings.setRecentRoot(repo.getRoot().getPath());
            }
        });
    }

    private void emptyTextAndTooltip() {
        myText = "";
        myTooltip = "";
    }

    @NotNull
    private static String getDisplayableBranchTooltip(GitRepository repo) {
        String text = GitBranchUtil.getDisplayableBranchText(repo);
        if (!GitUtil.justOneGitRepository(repo.getProject())) {
            return text + "\n" + "Root: " + repo.getRoot().getName();
        }
        return text;
    }




    private static class StartFeatureAction extends DumbAwareAction {
        Gitflow myGitflow = ServiceManager.getService(Gitflow.class);
        private final Project myProject;
        GitRepository repo;
        ArrayList<GitRepository> repos = new ArrayList<GitRepository>();

        StartFeatureAction(@NotNull Project project) {
            super("Start Feature");
            myProject = project;

            repo = GitBranchUtil.getCurrentRepository(myProject);
            repos.add(repo);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

            GitCommandResult res;

           String featureName = Messages.showInputDialog(myProject, "Enter the name of new feature:", "New Feature", Messages.getQuestionIcon(), "",
                    GitNewBranchNameValidator.newInstance(repos));

            if (featureName!=null){
               res=  myGitflow.startFeature(repo,featureName,null);
            }

        }

    }

    private static class FinishFeatureAction extends DumbAwareAction {
        Gitflow myGitflow = (Gitflow) ServiceManager.getService(Git.class);
        private final Project myProject;

        FinishFeatureAction(@NotNull Project project) {
            super("Finish Feature");
            myProject = project;
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

        }

    }
}
