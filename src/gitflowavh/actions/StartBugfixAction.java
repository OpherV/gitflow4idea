package gitflowavh.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.DialogWrapper;

import org.jetbrains.annotations.NotNull;

import git4idea.commands.GitCommandResult;
import gitflowavh.ui.GitFlowAVHStartBugfixDialog;
import gitflowavh.ui.NotifyUtil;

public class StartBugfixAction extends GitFlowAVHAction {

    public StartBugfixAction() {
        super("Start Bugfix");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        GitFlowAVHStartBugfixDialog dialog = new GitFlowAVHStartBugfixDialog(myProject);
        dialog.show();

        if (dialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) return;

        final String bugfixName = dialog.getNewBranchName();
        final String baseBranchName = dialog.getBaseBranchName();

        new Task.Backgroundable(myProject, "Starting bugfix " + bugfixName, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                createBugfixBranch(baseBranchName, bugfixName);
            }
        }.queue();
    }

    private void createBugfixBranch(String baseBranchName, String bugfixName) {
        GitFlowAVHErrorsListener errorListener = new GitFlowAVHErrorsListener(myProject);
        GitCommandResult result = myGitflow.startBugfix(repo, bugfixName, baseBranchName, errorListener);

        if (result.success()) {
            String startedBugfixMessage = String.format("A new branch '%s%s' was created, based on '%s'", bugfixPrefix, bugfixName, baseBranchName);
            NotifyUtil.notifySuccess(myProject, bugfixName, startedBugfixMessage);
        } else {
            NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
        }

        repo.update();
        virtualFileMananger.asyncRefresh(null); //update editors
    }
}