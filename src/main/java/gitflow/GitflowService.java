package gitflow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Fabien Marsaud / @fabmars
 * Only one instance, runActivity is called every time a project is opened
 */
@Service
public final class GitflowService implements StartupActivity {

  @Override
  public void runActivity(@NotNull Project project) {
    // Ensure this isn't part of testing
    if (!ApplicationManager.getApplication().isUnitTestMode()) {
      // Install Git Flow widget
      GitflowComponent gitflowComponent = new GitflowComponent(project);
      // Prepare for when the project will be closed
      Disposer.register(project, gitflowComponent);
    }
  }
}
