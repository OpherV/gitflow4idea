package gitflow.actions;

import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import gitflow.GitflowInitOptions;

public class ReInitRepoAction extends InitRepoAction {
    ReInitRepoAction() {
        super("Re-init Repo");
    }

    ReInitRepoAction(GitRepository repo) {
        super(repo, "Re-init Repo");
    }

    @Override
    protected String getPublishedFeatureMessage() {
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
