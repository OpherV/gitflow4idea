package gitflowavh.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import gitflowavh.GitFlowAVHConfigUtil;
import gitflowavh.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class FinishFeatureAction extends GitFlowAVHAction {

    String customFeatureName = null;

    FinishFeatureAction() {
        super("Finish Feature");
    }

    FinishFeatureAction(String name) {
        super("Finish Feature");
        customFeatureName = name;
    }

    /**
     * @param e AnActionEvent
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        String currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);
        if (!currentBranchName.isEmpty()) {

            final AnActionEvent event = e;
            final String featureName;
            // Check if a feature name was specified, otherwise take name from current branch
            if (customFeatureName != null) {
                featureName = customFeatureName;
            } else {
                featureName = GitFlowAVHConfigUtil.getFeatureNameFromBranch(myProject, currentBranchName);
            }
            final GitFlowAVHErrorsListener errorLineHandler = new GitFlowAVHErrorsListener(myProject);

            new Task.Backgroundable(myProject, "Finishing feature " + featureName, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitCommandResult result = myGitflow.finishFeature(repo, featureName, errorLineHandler);


                    if (result.success()) {
                        String finishedFeatureMessage = String.format("The feature branch '%s%s' was merged into '%s'", featurePrefix, featureName, developBranch);
                        NotifyUtil.notifySuccess(myProject, featureName, finishedFeatureMessage);
                    } else {
                        // (merge errors are handled in the onSuccess handler)
                        if (!errorLineHandler.hasMergeError) {
                            NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                        }
                    }

                    repo.update();

                }

                @Override
                public void onSuccess() {
                    super.onSuccess();

                    //merge conflicts if necessary
                    if (errorLineHandler.hasMergeError) {
                        if (handleMerge()) {
                            FinishFeatureAction completeFinishFeatureAction = new FinishFeatureAction(featureName);
                            completeFinishFeatureAction.actionPerformed(event);
                        }

                    }

                }
            }.queue();
        }

    }

}