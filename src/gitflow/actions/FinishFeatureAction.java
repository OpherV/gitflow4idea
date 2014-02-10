package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.VcsException;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import git4idea.merge.GitMerger;
import git4idea.util.GitUIUtil;
import gitflow.GitflowConfigUtil;
import org.jetbrains.annotations.NotNull;

public class FinishFeatureAction extends GitflowAction {

    String customFeatureName=null;

    FinishFeatureAction() {
        super("Finish Feature");
    }

    FinishFeatureAction(String name) {
        super("Finish Feature");
        customFeatureName=name;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        String currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);
        if (currentBranchName.isEmpty()==false){

            final AnActionEvent event=e;
            final String featureName;
            // Check if a feature name was specified, otherwise take name from current branch
            if (customFeatureName!=null){
                featureName = customFeatureName;
            }
            else{
                featureName = GitflowConfigUtil.getFeatureNameFromBranch(myProject, currentBranchName);
            }
            final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);

            new Task.Backgroundable(myProject,"Finishing feature "+featureName,false){
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitCommandResult result =  myGitflow.finishFeature(repo,featureName,errorLineHandler);


                    if (result.success()){
                        String finishedFeatureMessage = String.format("The feature branch '%s%s' was merged into '%s'", featurePrefix, featureName, developBranch);
                        GitUIUtil.notifySuccess(myProject, featureName, finishedFeatureMessage);
                    }
                    else if(errorLineHandler.hasMergeError){

                    }
                    else {

                        GitUIUtil.notifyError(myProject,"Error","Please have a look at the Version Control console for more details");

                    }

                }

                @Override
                public void onSuccess() {
                    super.onSuccess();


                    //ugly, but required for intellij to catch up with the external changes made by
                    //the CLI before being able to run the merge tool
                    virtualFileMananger.syncRefresh();
                    try {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException ignored) {
                    }


                    //TODO: refactor this logic to work in case of finishRelease as well
                    if (errorLineHandler.hasMergeError){
                        GitflowActions.runMergeTool();
                        repo.update();

                        //if merge was completed successfully, finish the action
                        //note that if it wasn't intellij is left in the "merging state", and git4idea provides no UI way to resolve it
                        int answer = Messages.showYesNoDialog(myProject, "Was the merge completed succesfully?", "Merge", Messages.getQuestionIcon());
                        if (answer==0){
                            GitMerger gitMerger=new GitMerger(myProject);

                            try {
                                gitMerger.mergeCommit(gitMerger.getMergingRoots());
                            } catch (VcsException e1) {
                                GitUIUtil.notifyError(myProject,"Error","Error committing merge result");
                                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }

                            FinishFeatureAction completeFinishFeatureAction = new FinishFeatureAction(featureName);
                            completeFinishFeatureAction.actionPerformed(event);

                        }


                    }
                }
            }.queue();
        }

    }

}