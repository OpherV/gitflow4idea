package gitflow;

import com.intellij.openapi.Disposable;
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


/**
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 * One instance per project
 */
public class GitflowComponent implements VcsListener, Disposable {
    Project myProject;
    GitflowWidget myGitflowWidget;
    MessageBus messageBus;

    public GitflowComponent(Project project) {
        myProject = project;
        messageBus = myProject.getMessageBus();
        messageBus.connect().subscribe(ProjectLevelVcsManager.VCS_CONFIGURATION_CHANGED, this);

        // Seems the event triggering this component happens after the directory mapping change, hence the incentive.
        directoryMappingChanged();
    }

    @Override
    public void dispose() {
        // TODO: insert component disposal logic here
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
                widgetToAdd = myGitflowWidget;
            } else {
                widgetToAdd = new GitflowUnsupportedVersionWidget(myProject);
            }

            if (statusBar != null) {
                statusBar.addWidget(widgetToAdd, StatusBar.Anchors.after(git4idea.ui.branch.GitBranchWidget.class.getName()), myProject);
            }
        } else {
            myGitflowWidget = null;
        }
    }

}
