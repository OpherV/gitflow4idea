package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.DialogWrapper;

import org.jetbrains.annotations.NotNull;

import git4idea.commands.GitCommandResult;
import gitflow.ui.GitflowNewFeatureHotfixOptionsDialog;
import gitflow.ui.NotifyUtil;

public class StartHotfixAction extends GitflowAction {

    StartHotfixAction() {
        super("Start Hotfix");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        GitflowNewFeatureHotfixOptionsDialog dialog =
                new GitflowNewFeatureHotfixOptionsDialog(myGitflow, repo, myProject, "hotfix");
        dialog.show();

        if (dialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) return;

        final String hotfixBranchName = dialog.getNewBranchName();
        final String baseBranchName = dialog.getBaseBranchName();

        new Task.Backgroundable(myProject, "Starting hotfix " + hotfixBranchName, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                createHotfixBranch(baseBranchName, hotfixBranchName);
            }
        }.queue();
    }

    private void createHotfixBranch(String baseBranchName, String hotfixBranchName) {
        GitflowErrorsListener errorListener = new GitflowErrorsListener(myProject);
        GitCommandResult result = myGitflow.startHotfix(repo, hotfixBranchName, errorListener);

        if (result.success()) {
            String startedHotfixMessage = String.format("A new hotfix '%s%s' was created, based on '%s'",
                    hotfixPrefix, hotfixBranchName, masterBranch);
            NotifyUtil.notifySuccess(myProject, hotfixBranchName, startedHotfixMessage);
        } else {
            NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
        }

        repo.update();
    }
}