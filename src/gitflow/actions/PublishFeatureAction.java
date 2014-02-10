package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.commands.GitCommandResult;
import git4idea.util.GitUIUtil;
import gitflow.GitflowConfigUtil;
import org.jetbrains.annotations.NotNull;

public class PublishFeatureAction extends GitflowAction {
    PublishFeatureAction(){
        super("Publish Feature");
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        final String featureName= GitflowConfigUtil.getFeatureNameFromBranch(myProject, currentBranchName);

        new Task.Backgroundable(myProject,"Publishing feature "+featureName,false){
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitCommandResult result = myGitflow.publishFeature(repo,featureName,new GitflowErrorsListener(myProject));

                if (result.success()){
                    String publishedFeatureMessage = String.format("A new remote branch '%s%s' was created", featurePrefix, featureName);
                    GitUIUtil.notifySuccess(myProject, featureName, publishedFeatureMessage);
                }
                else{
                    GitUIUtil.notifyError(myProject,"Error","Please have a look at the Version Control console for more details");
                }

                repo.update();


            }
        }.queue();

    }
}