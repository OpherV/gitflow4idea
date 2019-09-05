package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import git4idea.repo.GitRepository;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowBranchUtilManager;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractStartAction extends GitflowAction {
    AbstractStartAction(String actionName) {
        super(actionName);
    }

    AbstractStartAction(GitRepository repo, String actionName) {
        super(repo, actionName);
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        GitflowBranchUtil branchUtil = GitflowBranchUtilManager.getBranchUtil(myRepo);
        if (branchUtil != null) {
            //Disable and hide when gitflow has not been setup
            if (branchUtil.hasGitflow() == false) {
                e.getPresentation().setEnabledAndVisible(false);
            } else {
                e.getPresentation().setEnabledAndVisible(true);
            }
        }
    }
}
