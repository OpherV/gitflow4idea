package gitflow.actions;

import git4idea.repo.GitRepository;

public abstract class AbstractTrackAction extends AbstractBranchAction {
    AbstractTrackAction(String actionName, BranchType type) {
        super(actionName, type);
    }

    AbstractTrackAction(GitRepository repo, String actionName, BranchType type) {
        super(repo, actionName, type);
    }

    @Override
    protected boolean isActionAllowedForBranch() {
        String prefix;
        switch (type) {
            case Feature:
                prefix = branchUtil.getPrefixFeature();;
                break;
            case Release:
                prefix = branchUtil.getPrefixRelease();
                break;
            case Bugfix:
                prefix = branchUtil.getPrefixBugfix();
                break;
            default:
                return false;
        }

        boolean noRemoteBranches = branchUtil.getRemoteBranchesWithPrefix(prefix).isEmpty();
        boolean trackedAllBranches = branchUtil.areAllBranchesTracked(prefix);

        return noRemoteBranches == false && trackedAllBranches == false;
    }
}
