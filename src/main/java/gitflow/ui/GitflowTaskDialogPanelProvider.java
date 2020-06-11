package gitflow.ui;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.LocalTask;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.actions.vcs.VcsTaskDialogPanelProvider;
import com.intellij.tasks.ui.TaskDialogPanel;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowBranchUtilManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public class GitflowTaskDialogPanelProvider extends VcsTaskDialogPanelProvider {
    @Nullable
    @Override
    public TaskDialogPanel getOpenTaskPanel(@NotNull Project project, @NotNull LocalTask task) {
        return getTaskPanel(
                project,
                task,
                (p, t) -> (r -> new GitflowOpenTaskPanel(p, t, r))
        );
    }

    @Nullable
    @Override
    public TaskDialogPanel getCloseTaskPanel(@NotNull Project project, @NotNull LocalTask task) {
        return getTaskPanel(
                project,
                task,
                (p, t) -> (r -> new GitflowCloseTaskPanel(p, t, r))
        );
    }

    private TaskDialogPanel getTaskPanel(@NotNull Project project, @NotNull LocalTask task, BiFunction<Project, LocalTask, Function<GitRepository, TaskDialogPanel>> zz) {
        GitRepository currentRepo = GitBranchUtil.getCurrentRepository(project);
        GitflowBranchUtil branchUtil = GitflowBranchUtilManager.getBranchUtil(currentRepo);

        return branchUtil != null && branchUtil.hasGitflow() && TaskManager.getManager(project).isVcsEnabled()
                ? zz.apply(project, task).apply(currentRepo)
                : null;
    }
}
