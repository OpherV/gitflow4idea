package gitflow.actions;

import git4idea.repo.GitRepository;
import gitflow.GitflowBranchUtil;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBranchAction extends GitflowAction {
    enum BranchType {
        Feature, Release, Bugfix, Hotfix
    }

    BranchType type;

    AbstractBranchAction(String actionName, BranchType type) {
        super(actionName);
        this.type = type;
    }

    AbstractBranchAction(GitRepository repo, String actionName, BranchType type) {
        super(repo, actionName);
        this.type = type;
    }

    @Override
    boolean isActionAllowed(@NotNull GitflowBranchUtil branchUtil) {
        switch (type) {
            case Feature:
                return branchUtil.isCurrentBranchFeature();
            case Release:
                return branchUtil.isCurrentBranchRelease();
            case Bugfix:
                return branchUtil.isCurrentBranchBugfix();
            case Hotfix:
                return branchUtil.isCurrentBranchHotfix();
            default:
                return false;
        }
    }
}
