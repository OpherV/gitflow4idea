package gitflowavh.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.commands.GitCommandResult;
import gitflowavh.GitFlowAVHConfigUtil;
import gitflowavh.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class PublishHotfixAction extends GitFlowAVHAction {
    PublishHotfixAction() {
        super("Publish Hotfix");
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        final String hotfixName = GitFlowAVHConfigUtil.getHotfixNameFromBranch(myProject, currentBranchName);
        final GitFlowAVHErrorsListener errorLineHandler = new GitFlowAVHErrorsListener(myProject);

        new Task.Backgroundable(myProject, "Publishing hotfix " + hotfixName, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitCommandResult result = myGitflow.publishHotfix(repo, hotfixName, errorLineHandler);

                if (result.success()) {
                    String publishedHotfixMessage = String.format("A new remote branch '%s%s' was created", hotfixPrefix, hotfixName);
                    NotifyUtil.notifySuccess(myProject, hotfixName, publishedHotfixMessage);
                } else {
                    NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                }

                repo.update();
            }
        }.queue();

    }
}