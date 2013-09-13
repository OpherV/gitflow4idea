package gitflow;

import com.intellij.dvcs.DvcsUtil;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsListener;
import com.intellij.util.messages.MessageBus;
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
        //git repo present
        if (ProjectLevelVcsManager.getInstance(myProject).getAllVcsRoots().length>0){
            myGitflowWidget  = new GitflowWidget(myProject);
            DvcsUtil.installStatusBarWidget(myProject, myGitflowWidget );
        }
        else{
            if (myGitflowWidget!=null){
                DvcsUtil.removeStatusBarWidget(myProject, myGitflowWidget);
            }
            myGitflowWidget = null;
        }
    }
}