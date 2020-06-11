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


public class GitflowTaskDialogPanelProvider extends VcsTaskDialogPanelProvider {
    @Nullable
    @Override
    public TaskDialogPanel getOpenTaskPanel(@NotNull Project project, @NotNull LocalTask task) {
        GitRepository currentRepo = GitBranchUtil.getCurrentRepository(project);
        GitflowBranchUtil branchUtil = GitflowBranchUtilManager.getBranchUtil(currentRepo);
        return branchUtil != null && branchUtil.hasGitflow() && TaskManager.getManager(project).isVcsEnabled()
                ? new GitflowOpenTaskPanel(project, task, currentRepo)
                : null;
    }

    @Nullable
    @Override
    public TaskDialogPanel getCloseTaskPanel(@NotNull Project project, @NotNull LocalTask task) {
        GitRepository currentRepo = GitBranchUtil.getCurrentRepository(project);
        GitflowBranchUtil branchUtil = GitflowBranchUtilManager.getBranchUtil(currentRepo);
        return branchUtil != null && branchUtil.hasGitflow() && TaskManager.getManager(project).isVcsEnabled()
                ? new GitflowCloseTaskPanel(project, task, currentRepo)
                : null;
    }

}
