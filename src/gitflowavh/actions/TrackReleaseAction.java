package gitflowavh.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.commands.GitCommandResult;
import gitflowavh.GitFlowAVHConfigUtil;
import gitflowavh.ui.GitFlowAVHBranchChooseDialog;
import gitflowavh.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class TrackReleaseAction extends GitFlowAVHAction {

    public TrackReleaseAction(){
        super("Track Release");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        ArrayList<String> remoteBranches = branchUtil.getRemoteBranchNames();
        ArrayList<String> remoteReleaseBranches = new ArrayList<String>();

        // Get only the branches with the proper prefix
        for (String item : remoteBranches) {
            if (item.contains(releasePrefix)) {
                remoteReleaseBranches.add(item);
            }
        }

        if (remoteBranches.size()>0){
            GitFlowAVHBranchChooseDialog branchChoose = new GitFlowAVHBranchChooseDialog(myProject,remoteReleaseBranches);

            branchChoose.show();
            if (branchChoose.isOK()){
                String branchName= branchChoose.getSelectedBranchName();
                final String releaseName= GitFlowAVHConfigUtil.getReleaseNameFromBranch(myProject, branchName);
                final GitFlowAVHErrorsListener errorLineHandler = new GitFlowAVHErrorsListener(myProject);

                new Task.Backgroundable(myProject,"Tracking release "+releaseName,false){
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        GitCommandResult result = myGitflow.trackRelease(repo, releaseName, errorLineHandler);

                        if (result.success()) {
                            String trackedReleaseMessage = String.format(" A new remote tracking branch '%s%s' was created", releasePrefix, releaseName);
                            NotifyUtil.notifySuccess(myProject, releaseName, trackedReleaseMessage);
                        }
                        else {
                            NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                        }

                        repo.update();
                    }
                }.queue();
            }
        }
        else {
            NotifyUtil.notifyError(myProject, "Error", "No remote branches");
        }

    }
}