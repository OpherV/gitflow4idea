package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import git4idea.commands.GitCommandResult;
import git4idea.util.GitUIUtil;
import git4idea.validators.GitNewBranchNameValidator;
import org.jetbrains.annotations.NotNull;

public class StartFeatureAction extends GitflowAction {


    StartFeatureAction() {
        super("Start Feature");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        //set up context variables
        super.actionPerformed(e);

        final String featureName = Messages.showInputDialog(myProject, "Enter the name of new feature:", "New Feature", Messages.getQuestionIcon(), "",
                GitNewBranchNameValidator.newInstance(repos));

        if (featureName!=null && !featureName.isEmpty()){
            new Task.Backgroundable(myProject,"Starting feature "+featureName,false){
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitCommandResult result =  myGitflow.startFeature(repo,featureName,new GitflowErrorsListener(myProject));


                    if (result.success()){
                        String startedFeatureMessage = String.format("A new branch '%s%s' was created, based on '%s'", featurePrefix, featureName, developBranch);
                        GitUIUtil.notifySuccess(myProject, featureName, startedFeatureMessage);
                    }
                    else{
                        GitUIUtil.notifyError(myProject,"Error","Please have a look at the Version Control console for more details");
                    }

                    repo.update();

                }
            }.queue();

        }
        else{
            Messages.showWarningDialog(myProject, "You must provide a name for the feature", "Whoops");
        }

    }
}