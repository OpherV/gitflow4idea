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

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.impl.status.EditorBasedWidget;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.popup.PopupFactoryImpl;
import com.intellij.util.Consumer;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;

import git4idea.GitBranch;
import gitflow.*;
import gitflow.actions.GitflowPopupGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import git4idea.GitUtil;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import git4idea.ui.branch.GitBranchWidget;
import git4idea.GitVcs;

/**
 * Status bar widget which displays actions for git flow
 *
 * @author Kirill Likhodedov, Opher Vishnia, Alexander von Bremen-Kühne
 */
public class GitflowWidget extends GitBranchWidget implements GitRepositoryChangeListener, StatusBarWidget.TextPresentation {
    private volatile String myText = "";
    private volatile String myTooltip = "";

    private GitflowPopupGroup popupGroup;

    public GitflowWidget(@NotNull Project project) {
        super(project);
        project.getMessageBus().connect().subscribe(GitRepository.GIT_REPO_CHANGE, this);
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
    public WidgetPresentation getPresentation() {
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
        initVersionCheck();
        updateAsync();
    }

    @Nullable
    @ Override
    public ListPopup getPopupStep() {
        Project project = IDEAUtils.getActiveProject();

        if (project == null) {
            return null;
        }
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);
        if (repo == null) {
            return null;
        }

        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (focusOwner == null) {
            IdeFocusManager focusManager = IdeFocusManager.getInstance(project);
            Window frame = focusManager.getLastFocusedIdeWindow();
            if (frame != null) {
                focusOwner = focusManager.getLastFocusedFor(frame);
            }
        }

        DataContext dataContext = DataManager.getInstance().getDataContext(focusOwner);
        ListPopup listPopup = new PopupFactoryImpl.ActionGroupPopup("Gitflow Actions", popupGroup.getActionGroup(), dataContext, false, false, false, true, null, -1,
                null, null);

        return listPopup;
    }

    @NotNull
    @Override
    public String getSelectedValue() {
        if (getHasVersionBeenTested() && !getIsSupportedVersion()) {
            return "Unsupported Git Flow Version";
        }
        return myText;
    }

    @Override
    public String getTooltipText() {
        if (!getIsSupportedVersion()) {
            return "Click for details";
        }
        return myTooltip;
    }

    @NotNull
    @Override
    public Consumer<MouseEvent> getClickConsumer() {
        return mouseEvent -> {
            if (getIsSupportedVersion()) {
                final ListPopup popup = getPopupStep();
                if (popup == null) return;
                final Dimension dimension = popup.getContent().getPreferredSize();
                final Point at = new Point(0, -dimension.height);
                popup.show(new RelativePoint(mouseEvent.getComponent(), at));
            } else {
                MessageDialogBuilder.YesNo builder = MessageDialogBuilder.yesNo("Unsupported Git Flow version", "The Git Flow CLI version installed isn't supported by the Git Flow Integration plugin")
                        .yesText("More information (open browser)")
                        .noText("no");
                if (builder.show() == Messages.OK) {
                    BrowserUtil.browse("https://github.com/OpherV/gitflow4idea/blob/develop/GITFLOW_VERSION.md");
                }
            }
        };

    }

    public void updateAsync() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                update();

                if (myStatusBar != null){
                    myStatusBar.updateWidget(ID());
                }
            }
        });
    }

    private void update() {
        Project project = getProject();

        //repopulate the branchUtil
        GitflowBranchUtilManager.update(project);

        if (project == null) {
            emptyTextAndTooltip();
            return;
        }

        GitRepository repo = GitBranchUtil.getCurrentRepository(project);
        if (repo == null) { // the file is not under version control => display nothing
            emptyTextAndTooltip();
            return;
        }


        //No advanced features in the status-bar widget
        popupGroup = new GitflowPopupGroup(project, false);

        GitflowBranchUtil gitflowBranchUtil = GitflowBranchUtilManager.getBranchUtil(repo);
        boolean hasGitflow = gitflowBranchUtil.hasGitflow();

        myText = hasGitflow ? "Gitflow" : "No Gitflow";
        myTooltip = getDisplayableBranchTooltip(repo);
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

    @NotNull
    @Override
    public String getText() {
        return getSelectedValue();
    }

    @Override
    public float getAlignment() {
        return 0;
    }

    public boolean getIsSupportedVersion(){
        GitflowVersionTester versionTester = GitflowVersionTester.forProject(myProject);
        return versionTester.hasVersionBeenTested() && versionTester.isSupportedVersion();
    }

    public boolean getHasVersionBeenTested(){
        return GitflowVersionTester.forProject(myProject).hasVersionBeenTested();
    }

    private void initVersionCheck(){

        // init the gitflow cli version check in a new thread and not on the EDT
        String version = GitflowVersionTester.forProject(myProject).getVersion();
        if (version == null) {
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    VcsRoot[] vcsRoots = ProjectLevelVcsManager.getInstance(myProject).getAllVcsRoots();
                    if (vcsRoots.length > 0 && vcsRoots[0].getVcs() instanceof GitVcs) {
                        GitflowVersionTester.forProject(myProject).init();
                    }

                }
            };
            new Thread(runnable).start();
        }
    }

}
