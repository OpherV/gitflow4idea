package gitflow.actions;

import git4idea.repo.GitRepository;

abstract class AbstractStartAction extends GitflowAction {
    AbstractStartAction(String actionName) {
        super(actionName);
    }

    AbstractStartAction(GitRepository repo, String actionName) {
        super(repo, actionName);
    }
}
