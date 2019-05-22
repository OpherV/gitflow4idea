package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import git4idea.repo.GitRepository;
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
    public void update(@NotNull AnActionEvent e) {
        if (branchUtil == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }

        //Disable and hide when gitflow has not been setup
        if (branchUtil.hasGitflow() == false) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }

        //Disable and hide when the branch-type is incorrect
        if (isActionAllowedForBranch() == false) {
            e.getPresentation().setEnabledAndVisible(false);
        } else {
            e.getPresentation().setEnabledAndVisible(true);
        }
    }

    protected boolean isActionAllowedForBranch() {
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
