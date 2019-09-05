package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import gitflow.GitflowInitOptions;
import org.jetbrains.annotations.NotNull;

public class ReInitRepoAction extends InitRepoAction {
    ReInitRepoAction() {
        super("Re-init Repo");
    }

    ReInitRepoAction(GitRepository repo) {
        super(repo, "Re-init Repo");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        gitflow.GitflowBranchUtil branchUtil = gitflow.GitflowBranchUtilManager.getBranchUtil(myRepo);

        // Only show when gitflow is setup
        if (branchUtil.hasGitflow()) {
            e.getPresentation().setEnabledAndVisible(true);
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }

    @Override
    protected String getSuccessMessage() {
        return "Re-initialized gitflow in repo " + myRepo.getRoot().getPresentableName();
    }

    @Override
    protected GitCommandResult executeCommand(GitflowInitOptions initOptions,
            GitflowErrorsListener errorLineHandler,
            GitflowLineHandler localLineHandler) {
        return myGitflow.reInitRepo(myRepo, initOptions, errorLineHandler, localLineHandler);
    }

    @Override
    protected String getTitle() {
        return "Re-initializing Repo";
    }

    @Override
    protected GitflowLineHandler getLineHandler() {
        return new GitflowErrorsListener(myProject);
    }
}
