package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import gitflow.GitflowConfigUtil;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class PublishReleaseAction extends AbstractPublishAction {

    PublishReleaseAction(){
        super("Publish Release", BranchType.Release);
    }

    PublishReleaseAction(GitRepository repo){
        super(repo,"Publish Release", BranchType.Release);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        GitflowConfigUtil gitflowConfigUtil = GitflowConfigUtil.getInstance(myProject, myRepo);
        final String releaseName= gitflowConfigUtil.getReleaseNameFromBranch(branchUtil.getCurrentBranchName());
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);

        new Task.Backgroundable(myProject,"Publishing release "+releaseName,false){
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitCommandResult result = myGitflow.publishRelease(myRepo, releaseName, errorLineHandler);

                if (result.success()) {
                    String publishedReleaseMessage = String.format("A new remote branch '%s%s' was created", branchUtil.getPrefixRelease(), releaseName);
                    NotifyUtil.notifySuccess(myProject, releaseName, publishedReleaseMessage);
                }
                else {
                    NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                }

                myRepo.update();
            }
        }.queue();

    }
}