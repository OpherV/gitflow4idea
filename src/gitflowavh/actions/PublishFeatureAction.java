package gitflowavh.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.commands.GitCommandResult;
import gitflowavh.GitFlowAVHConfigUtil;
import gitflowavh.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class PublishFeatureAction extends GitFlowAVHAction {
    PublishFeatureAction(){
        super("Publish Feature");
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        final String featureName= GitFlowAVHConfigUtil.getFeatureNameFromBranch(myProject, currentBranchName);

        new Task.Backgroundable(myProject,"Publishing feature "+featureName,false){
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitCommandResult result = myGitflow.publishFeature(repo,featureName,new GitFlowAVHErrorsListener(myProject));

                if (result.success()) {
                    String publishedFeatureMessage = String.format("A new remote branch '%s%s' was created", featurePrefix, featureName);
                    NotifyUtil.notifySuccess(myProject, featureName, publishedFeatureMessage);
                }
                else {
                    NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                }

                repo.update();


            }
        }.queue();

    }
}