package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.VcsException;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import git4idea.merge.GitMerger;
import git4idea.repo.GitRepository;
import gitflow.GitflowConfigUtil;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class FinishFeatureAction extends GitflowAction {

    String customFeatureName=null;

    public FinishFeatureAction() {
        super("Finish Feature");
    }

    public FinishFeatureAction(GitRepository repo) {
        super(repo, "Finish Feature");
    }

    FinishFeatureAction(GitRepository repo, String name) {
        super(repo, "Finish Feature");
        customFeatureName=name;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        String currentBranchName = GitBranchUtil.getBranchNameOrRev(myRepo);
        if (currentBranchName.isEmpty()==false){

            final AnActionEvent event=e;
            final String featureName;
            // Check if a feature name was specified, otherwise take name from current branch
            if (customFeatureName!=null){
                featureName = customFeatureName;
            }
            else{
                featureName = GitflowConfigUtil.getFeatureNameFromBranch(myProject, myRepo, currentBranchName);
            }

            this.runAction(myProject, featureName);
        }

    }

    public void runAction(final Project project, final String featureName){
        super.runAction(project, null, featureName);

        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);
        final FinishFeatureAction that = this;

        //get the base branch for this feature
        final String baseBranch = GitflowConfigUtil.getBaseBranch(project, myRepo, featurePrefix+featureName);

        new Task.Backgroundable(myProject,"Finishing feature "+featureName,false){
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitCommandResult result =  myGitflow.finishFeature(myRepo,featureName,errorLineHandler);


                if (result.success()) {
                    String finishedFeatureMessage = String.format("The feature branch '%s%s' was merged into '%s'", featurePrefix, featureName, baseBranch);
                    NotifyUtil.notifySuccess(myProject, featureName, finishedFeatureMessage);
                }
                else if(errorLineHandler.hasMergeError){
                    // (merge errors are handled in the onSuccess handler)
                }
                else {
                    NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                }

                myRepo.update();

            }

            @Override
            public void onSuccess() {
                super.onSuccess();

                //merge conflicts if necessary
                if (errorLineHandler.hasMergeError){
                    if (handleMerge()){
                        that.runAction(project, featureName);
                        FinishFeatureAction completeFinishFeatureAction = new FinishFeatureAction(myRepo, featureName);
                    }

                }

            }
        }.queue();;
    }

}