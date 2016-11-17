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
package gitflow.ui;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.popup.PopupFactoryImpl;
import com.intellij.util.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import git4idea.GitUtil;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import git4idea.ui.branch.GitBranchWidget;
import gitflow.actions.GitflowActions;

/**
 * Status bar widget which displays actions for git flow
 *
 * @author Kirill Likhodedov, Opher Vishnia, Alexander von Bremen-Kühne
 */
public class GitflowWidget extends GitBranchWidget implements GitRepositoryChangeListener {
    private volatile String myText = "";
    private volatile String myTooltip = "";

    private GitflowActions actions;

    public GitflowWidget(@NotNull Project project) {
        super(project);
        project.getMessageBus().connect().subscribe(GitRepository.GIT_REPO_CHANGE, this);
        updateAsync();
    }

    @Override
    public StatusBarWidget copy() {
        return new GitBranchWidget(getProject());
    }

    @NotNull
    @Override
    public String ID() {
        return getWidgetID();
    }

    @Override
    public WidgetPresentation getPresentation(@NotNull PlatformType type) {
        return this;
    }

    @Override
    public void selectionChanged(FileEditorManagerEvent event) {
        //updateAsync();
    }

    @Override
    public void fileOpened(FileEditorManager source, VirtualFile file) {
        //updateAsync();
    }

    @Override
    public void fileClosed(FileEditorManager source, VirtualFile file) {
        //updateAsync();
    }

    @Override
    public void repositoryChanged(@NotNull GitRepository repository) {
        updateAsync();
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

        if (actions == null)
            return null;

        ActionGroup popupGroup = actions.getActions();
        ListPopup listPopup = new PopupFactoryImpl.ActionGroupPopup("Gitflow Actions", popupGroup, SimpleDataContext.getProjectContext(project), false, false, false, true, null, -1,
                null, null);

        return listPopup;
    }

    @Override
    public String getSelectedValue() {
        return myText;
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
                updateAsync();
            }
        };
    }

    private void updateAsync() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                update();
            }
        });
    }

    private void update() {
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

        actions = new GitflowActions(project);

        boolean hasGitflow = actions.hasGitflow();

        myText = hasGitflow ? "Gitflow" : "No Gitflow";
        myTooltip = getDisplayableBranchTooltip(repo);

        if (myStatusBar != null){
            myStatusBar.updateWidget(ID());
        }
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

    @NotNull
    private static String getWidgetID() {
        return GitflowWidget.class.getName();
    }

    /**
     * This method looks up the widget instance for a specific project
     *
     * @param project The project for which the widget instance should be looked up
     * @return The widget instance for the provided project or null if no instance is available
     */
    @Nullable
    public static GitflowWidget findWidgetInstance(@Nullable Project project) {
        if (project != null) {
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

            if (statusBar != null) {
                StatusBarWidget possibleWidget = statusBar.getWidget(getWidgetID());
                if (possibleWidget instanceof GitflowWidget)
                    return (GitflowWidget) possibleWidget;
            }
        }

        return null;
    }

    /**
     * Shows the action popup of this widget in the center of the provided frame. If there are no
     * actions available for this widget, the popup will not be shown.
     *
     * @param frame The frame that will be used for display
     */
    public void showPopupInCenterOf(@NotNull JFrame frame) {
        update();
        ListPopup popupStep = getPopupStep();
        if (popupStep != null)
            popupStep.showInCenterOf(frame);
    }
}
