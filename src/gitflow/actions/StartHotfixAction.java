package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import git4idea.commands.GitCommandResult;
import gitflow.ui.GitflowStartHotfixDialog;
import gitflow.ui.NotifyUtil;


public class StartHotfixAction extends GitflowAction {

    public StartHotfixAction(GitRepository repo) {
        super(repo, "Start Hotfix");
    }

    public StartHotfixAction() {
        super("Start Hotfix");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        GitflowStartHotfixDialog dialog = new GitflowStartHotfixDialog(myProject, myRepo);
        dialog.show();

        if (dialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) return;

        final String hotfixName = dialog.getNewBranchName();
        final String baseBranchName = dialog.getBaseBranchName();

        this.runAction(e.getProject(), baseBranchName, hotfixName);
    }

    public void runAction(Project project, final String baseBranchName, final String hotfixName){
        super.runAction(project, baseBranchName, hotfixName);

        new Task.Backgroundable(myProject, "Starting hotfix " + hotfixName, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                createHotfixBranch(baseBranchName, hotfixName);
            }
        }.queue();
    }

    private void createHotfixBranch(String baseBranchName, String hotfixBranchName) {
        GitflowErrorsListener errorListener = new GitflowErrorsListener(myProject);
        GitCommandResult result = myGitflow.startHotfix(myRepo, hotfixBranchName, baseBranchName, errorListener);

        if (result.success()) {
            String startedHotfixMessage = String.format("A new hotfix '%s%s' was created, based on '%s'",
                    hotfixPrefix, hotfixBranchName, baseBranchName);
            NotifyUtil.notifySuccess(myProject, hotfixBranchName, startedHotfixMessage);
        } else {
            NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
        }

        myRepo.update();
    }
}