package gitflow;

import com.intellij.dvcs.DvcsUtil;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsListener;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.messages.MessageBus;
import git4idea.GitVcs;
import gitflow.ui.GitflowWidget;
import org.jetbrains.annotations.NotNull;

/**
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */
public class GitflowComponent implements ProjectComponent, VcsListener {
    Project myProject;
    GitflowWidget myGitflowWidget;
    MessageBus messageBus;

    public GitflowComponent(Project project) {
        myProject = project;
    }

    public void initComponent() {
        messageBus = myProject.getMessageBus();
        messageBus.connect().subscribe(ProjectLevelVcsManager.VCS_CONFIGURATION_CHANGED, this);
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "GitflowComponent";
    }

    public void projectOpened() {

    }

    public void projectClosed() {

    }

    @Override
    public void directoryMappingChanged() {
        VcsRoot[] vcsRoots=ProjectLevelVcsManager.getInstance(myProject).getAllVcsRoots();

        //git repo present
        if (vcsRoots.length>0 && vcsRoots[0].getVcs() instanceof GitVcs){

            myGitflowWidget  = new GitflowWidget(myProject);
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(myProject);
            if (statusBar != null) {
                statusBar.addWidget(myGitflowWidget, "after " + git4idea.ui.branch.GitBranchWidget.class.getName(), myProject);
            }
        }
        else{
            if (myGitflowWidget!=null){
                DvcsUtil.removeStatusBarWidget(myProject, myGitflowWidget);
            }
            myGitflowWidget = null;
        }
    }
}