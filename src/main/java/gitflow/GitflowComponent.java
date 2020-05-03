package gitflow;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsListener;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.messages.MessageBus;
import gitflow.ui.GitflowWidget;
import org.jetbrains.annotations.NotNull;


/**
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */
public class GitflowComponent implements ProjectComponent, VcsListener {
    Project myProject;
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
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(myProject);
        GitflowWidget widget = (GitflowWidget) statusBar.getWidget(GitflowWidget.class.getName());
        if (widget != null) {
            widget.updateAsync();
        } else {
            throw new NullPointerException("widget");
        }
    }
}
