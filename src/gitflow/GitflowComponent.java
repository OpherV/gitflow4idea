package gitflow;

import com.intellij.dvcs.DvcsUtil;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import gitflow.ui.GitflowWidget;
import org.jetbrains.annotations.NotNull;

/**
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */
public class GitflowComponent implements ProjectComponent {
    Project myProject;
    GitflowWidget myGitflowWidget;

    public GitflowComponent(Project project) {
        myProject = project;
    }

    public void initComponent() {

    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "GitflowComponent";
    }

    public void projectOpened() {
        myGitflowWidget  = new GitflowWidget(myProject);
        DvcsUtil.installStatusBarWidget(myProject, myGitflowWidget );
    }

    public void projectClosed() {
        DvcsUtil.removeStatusBarWidget(myProject, myGitflowWidget);
        myGitflowWidget = null;
    }
}
