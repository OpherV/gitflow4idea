package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import gitflow.GitflowConfigUtil;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class PublishFeatureAction extends AbstractPublishAction {
    PublishFeatureAction(){
        super("Publish Feature", BranchType.Feature);
    }

    PublishFeatureAction(GitRepository repo){
        super(repo, "Publish Feature", BranchType.Feature);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        GitflowConfigUtil gitflowConfigUtil = GitflowConfigUtil.getInstance(myProject, myRepo);
        final String featureName= gitflowConfigUtil.getFeatureNameFromBranch(branchUtil.getCurrentBranchName());

        new Task.Backgroundable(myProject,"Publishing feature "+featureName,false){
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitCommandResult result = myGitflow.publishFeature(myRepo, featureName,new GitflowErrorsListener(myProject));

                if (result.success()) {
                    String publishedFeatureMessage = String.format("A new remote branch '%s%s' was created", branchUtil.getPrefixFeature(), featureName);
                    NotifyUtil.notifySuccess(myProject, featureName, publishedFeatureMessage);
                }
                else {
                    NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                }

                myRepo.update();


            }
        }.queue();

    }
}