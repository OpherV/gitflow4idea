package gitflowavh.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.DialogWrapper;
import git4idea.commands.GitCommandResult;
import gitflowavh.ui.GitFlowAVHDeleteBugfixDialog;
import gitflowavh.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class DeleteBugfixAction extends GitFlowAVHAction {
    public DeleteBugfixAction() {
        super("Finish Bugfix");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        GitFlowAVHDeleteBugfixDialog dialog = new GitFlowAVHDeleteBugfixDialog(myProject);
        dialog.show();

        if (dialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) return;

        final String branchName = dialog.getBranchName(true);
        final boolean forceDelete = dialog.isForceDeleteChecked();

        new Task.Backgroundable(myProject, "Deleting bugfix " + branchName, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                deleteBranch(branchName, forceDelete);
            }
        }.queue();
    }

    private void deleteBranch(String branchName, boolean forceDelete) {
        GitFlowAVHErrorsListener errorListener = new GitFlowAVHErrorsListener(myProject);
        GitCommandResult result = myGitflow.deleteBugfix(repo, branchName, forceDelete, errorListener);

        if (result.success()) {
            String deletedBugfixMessage = String.format("Bugfix branch '%s' was deleted.", branchName);
            NotifyUtil.notifySuccess(myProject, branchName, deletedBugfixMessage);
        } else {
            NotifyUtil.notifyError(myProject, "Error", result.toString());
        }

        repo.update();
        virtualFileMananger.asyncRefresh(null); // Update editors
    }
}
