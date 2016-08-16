package gitflow.ui;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.LocalTask;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.ui.TaskDialogPanel;
import com.intellij.tasks.ui.TaskDialogPanelProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GitflowTaskDialogPanelProvider extends TaskDialogPanelProvider{

    @Nullable
    @Override
    public TaskDialogPanel getOpenTaskPanel(@NotNull Project project, @NotNull Task task) {
        return TaskManager.getManager(project).isVcsEnabled() ? new GitflowOpenTaskPanel(project, task) : null;
    }

    @Nullable
    @Override
    public TaskDialogPanel getCloseTaskPanel(@NotNull Project project, @NotNull LocalTask localTask) {
        return null; //TaskManager.getManager(project).isVcsEnabled() ? new VcsCloseTaskPanel(project, task) : null;
    }
}
