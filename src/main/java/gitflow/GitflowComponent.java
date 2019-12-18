package gitflow;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsListener;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.messages.MessageBus;
import git4idea.GitVcs;
import gitflow.ui.GitflowUnsupportedVersionWidget;
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
        VcsRoot[] vcsRoots = ProjectLevelVcsManager.getInstance(myProject).getAllVcsRoots();
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(myProject);

        //git repo present
        if (vcsRoots.length > 0 && vcsRoots[0].getVcs() instanceof GitVcs) {


            StatusBarWidget widgetToAdd;

            //make sure to not reinitialize the widget if it's already present
            if (GitflowVersionTester.forProject(myProject).isSupportedVersion() && myGitflowWidget == null) {
                myGitflowWidget = new GitflowWidget(myProject);
                widgetToAdd = (StatusBarWidget) myGitflowWidget;
            } else {
                widgetToAdd = new GitflowUnsupportedVersionWidget(myProject);
            }

            if (statusBar != null) {
                statusBar.addWidget(widgetToAdd, "after " + git4idea.ui.branch.GitBranchWidget.class.getName(), myProject);
            }
        } else {
            if (myGitflowWidget != null) {
                myGitflowWidget.deactivate();
            }
            myGitflowWidget = null;
        }
    }

}
