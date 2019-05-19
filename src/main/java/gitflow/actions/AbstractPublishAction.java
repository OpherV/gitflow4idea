package gitflow.actions;

import git4idea.repo.GitRepository;
import gitflow.GitflowBranchUtil;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPublishAction extends AbstractBranchAction {
    AbstractPublishAction(String actionName, BranchType type) {
        super(actionName, type);
    }

    AbstractPublishAction(GitRepository repo, String actionName, BranchType type) {
        super(repo, actionName, type);
    }

    @Override
    protected boolean isActionAllowed(@NotNull GitflowBranchUtil branchUtil) {
        if (!super.isActionAllowed(branchUtil)) {
            return false;
        }

        return !branchUtil.isCurrentBranchPublished();
    }
}
