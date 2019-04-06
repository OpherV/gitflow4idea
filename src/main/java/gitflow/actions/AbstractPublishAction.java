package gitflow.actions;

import git4idea.repo.GitRepository;

public abstract class AbstractPublishAction extends AbstractBranchAction {
    AbstractPublishAction(String actionName, BranchType type) {
        super(actionName, type);
    }

    AbstractPublishAction(GitRepository repo, String actionName, BranchType type) {
        super(repo, actionName, type);
    }

    @Override
    protected boolean isActionAllowedForBranch() {
        if (!super.isActionAllowedForBranch()) {
            return false;
        }
        
        return !branchUtil.isCurrentBranchPublished();
    }
}
