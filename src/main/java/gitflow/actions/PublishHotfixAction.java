package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.commands.Git;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import gitflow.GitflowConfigUtil;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class PublishHotfixAction extends AbstractPublishAction {
    PublishHotfixAction() {
        super("Publish Hotfix", BranchType.Hotfix);
    }

    PublishHotfixAction(GitRepository repo) {
        super(repo, "Publish Hotfix", BranchType.Hotfix);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        GitflowConfigUtil gitflowConfigUtil = GitflowConfigUtil.getInstance(myProject, myRepo);
        final String hotfixName = gitflowConfigUtil.getHotfixNameFromBranch(branchUtil.getCurrentBranchName());
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);

        new Task.Backgroundable(myProject, "Publishing hotfix " + hotfixName, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitCommandResult result = myGitflow.publishHotfix(myRepo, hotfixName, errorLineHandler);

                if (result.success()) {
                    String publishedHotfixMessage = String.format("A new remote branch '%s%s' was created", branchUtil.getPrefixHotfix(), hotfixName);
                    NotifyUtil.notifySuccess(myProject, hotfixName, publishedHotfixMessage);
                } else {
                    NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                }

                myRepo.update();
            }
        }.queue();

    }
}