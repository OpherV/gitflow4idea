package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import gitflow.ui.GitflowStartBugfixDialog;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StartBugfixAction extends GitflowAction {

    public StartBugfixAction() {
        super("Start Bugfix");
    }
    public StartBugfixAction(GitRepository repo) {
        super(repo, "Start Bugfix");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        GitflowStartBugfixDialog dialog = new GitflowStartBugfixDialog(myProject, myRepo);
        dialog.show();

        if (dialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) return;

        final String bugfixName = dialog.getNewBranchName();
        final String baseBranchName = dialog.getBaseBranchName();

        this.runAction(e.getProject(), baseBranchName, bugfixName, null);
    }

    public void runAction(Project project, final String baseBranchName, final String bugfixName, @Nullable final Runnable callInAwtLater){
        super.runAction(project, baseBranchName, bugfixName, callInAwtLater);

        new Task.Backgroundable(myProject, "Starting bugfix " + bugfixName, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                final GitCommandResult commandResult = createBugfixBranch(baseBranchName, bugfixName);
                if (callInAwtLater != null && commandResult.success()) {
                    callInAwtLater.run();
                }
            }
        }.queue();
    }

    private GitCommandResult createBugfixBranch(String baseBranchName, String bugfixName) {
        GitflowErrorsListener errorListener = new GitflowErrorsListener(myProject);
        GitCommandResult result = myGitflow.startBugfix(myRepo, bugfixName, baseBranchName, errorListener);

        if (result.success()) {
            String startedBugfixMessage = String.format("A new branch '%s%s' was created, based on '%s'", bugfixPrefix, bugfixName, baseBranchName);
            NotifyUtil.notifySuccess(myProject, bugfixName, startedBugfixMessage);
        } else {
            NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
        }

        myRepo.update();
        virtualFileMananger.asyncRefresh(null); //update editors
        return result;
    }
}