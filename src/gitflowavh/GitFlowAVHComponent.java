package gitflowavh;

import com.intellij.dvcs.DvcsUtil;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsListener;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.messages.MessageBus;
import git4idea.GitVcs;
import gitflowavh.ui.GitFlowAVHWidget;
import org.jetbrains.annotations.NotNull;


public class GitFlowAVHComponent implements ProjectComponent, VcsListener {
    Project myProject;
    GitFlowAVHWidget myGitflowWidget;
    MessageBus messageBus;

    /**
     * @param project Project
     */
    public GitFlowAVHComponent(Project project) {
        myProject = project;
    }

    public void initComponent() {
        messageBus = myProject.getMessageBus();
        messageBus.connect().subscribe(ProjectLevelVcsManager.VCS_CONFIGURATION_CHANGED, this);
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    /**
     * @return String
     */
    @NotNull
    public String getComponentName() {
        return "GitFlowAVHComponent";
    }

    public void projectOpened() {

    }

    public void projectClosed() {

    }

    @Override
    public void directoryMappingChanged() {
        VcsRoot[] vcsRoots = ProjectLevelVcsManager.getInstance(myProject).getAllVcsRoots();

        //git repo present
        if (vcsRoots.length > 0 && vcsRoots[0].getVcs() instanceof GitVcs) {

            myGitflowWidget = new GitFlowAVHWidget(myProject);
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(myProject);
            if (statusBar != null) {
                statusBar.addWidget(myGitflowWidget, "after " + git4idea.ui.branch.GitBranchWidget.class.getName(), myProject);
            }
        } else {
            if (myGitflowWidget != null) {
                DvcsUtil.removeStatusBarWidget(myProject, myGitflowWidget);
            }
            myGitflowWidget = null;
        }
    }
}