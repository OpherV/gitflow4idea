package gitflowavh.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRemote;
import gitflowavh.GitFlowAVHConfigUtil;
import gitflowavh.ui.GitFlowAVHBranchChooseDialog;
import gitflowavh.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class TrackFeatureAction extends GitFlowAVHAction {

    public TrackFeatureAction() {
        super("Track Feature");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        ArrayList<String> remoteBranches = branchUtil.getRemoteBranchNames();
        ArrayList<String> remoteFeatureBranches = new ArrayList<String>();

        //get only the branches with the proper prefix
        for (String item : remoteBranches) {
            if (item.contains(featurePrefix)) {
                remoteFeatureBranches.add(item);
            }
        }

        if (remoteBranches.size() > 0) {
            GitFlowAVHBranchChooseDialog branchChoose = new GitFlowAVHBranchChooseDialog(myProject, remoteFeatureBranches);

            branchChoose.show();
            if (branchChoose.isOK()) {
                String branchName = branchChoose.getSelectedBranchName();
                final String featureName = GitFlowAVHConfigUtil.getFeatureNameFromBranch(myProject, branchName);
                final GitRemote remote = branchUtil.getRemoteByBranch(branchName);
                final GitFlowAVHErrorsListener errorLineHandler = new GitFlowAVHErrorsListener(myProject);

                new Task.Backgroundable(myProject, "Tracking feature " + featureName, false) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        GitCommandResult result = myGitflow.trackFeature(repo, featureName, remote, errorLineHandler);

                        if (result.success()) {
                            String trackedFeatureMessage = String.format("A new branch '%s%s' was created", featurePrefix, featureName);
                            NotifyUtil.notifySuccess(myProject, featureName, trackedFeatureMessage);
                        } else {
                            NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                        }

                        repo.update();

                    }
                }.queue();
            }
        } else {
            NotifyUtil.notifyError(myProject, "Error", "No remote branches");
        }

    }
}