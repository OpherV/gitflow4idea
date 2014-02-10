package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAwareAction;
import git4idea.commands.GitCommandResult;
import git4idea.util.GitUIUtil;
import gitflow.GitflowConfigUtil;
import gitflow.actions.GitflowAction;
import gitflow.actions.GitflowErrorsListener;
import org.jetbrains.annotations.NotNull;

public class PublishHotfixAction extends GitflowAction {
    PublishHotfixAction() {
        super("Publish Hotfix");
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        final String hotfixName = GitflowConfigUtil.getHotfixNameFromBranch(myProject, currentBranchName);
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);

        new Task.Backgroundable(myProject, "Publishing hotfix " + hotfixName, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitCommandResult result = myGitflow.publishHotfix(repo, hotfixName, errorLineHandler);

                if (result.success()) {
                    String publishedHotfixMessage = String.format("A new remote branch '%s%s' was created", hotfixPrefix, hotfixName);
                    GitUIUtil.notifySuccess(myProject, hotfixName, publishedHotfixMessage);
                } else {
                    GitUIUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                }

                repo.update();
            }
        }.queue();

    }
}