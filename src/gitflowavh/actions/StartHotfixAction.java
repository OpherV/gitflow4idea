package gitflowavh.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.DialogWrapper;

import org.jetbrains.annotations.NotNull;

import git4idea.commands.GitCommandResult;
import gitflowavh.ui.GitFlowAVHStartHotfixDialog;
import gitflowavh.ui.NotifyUtil;


public class StartHotfixAction extends GitFlowAVHAction {

    StartHotfixAction() {
        super("Start Hotfix");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        GitFlowAVHStartHotfixDialog dialog = new GitFlowAVHStartHotfixDialog(myProject);
        dialog.show();

        if (dialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) return;

        final String hotfixName = dialog.getNewBranchName();
        final String baseBranchName = dialog.getBaseBranchName();

        new Task.Backgroundable(myProject, "Starting hotfix " + hotfixName, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                createHotfixBranch(baseBranchName, hotfixName);
            }
        }.queue();
    }

    private void createHotfixBranch(String baseBranchName, String hotfixBranchName) {
        GitFlowAVHErrorsListener errorListener = new GitFlowAVHErrorsListener(myProject);
        GitCommandResult result = myGitflow.startHotfix(repo, hotfixBranchName, baseBranchName, errorListener);

        if (result.success()) {
            String startedHotfixMessage = String.format("A new hotfix '%s%s' was created, based on '%s'",
                    hotfixPrefix, hotfixBranchName, baseBranchName);
            NotifyUtil.notifySuccess(myProject, hotfixBranchName, startedHotfixMessage);
        } else {
            NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
        }

        repo.update();
    }
}