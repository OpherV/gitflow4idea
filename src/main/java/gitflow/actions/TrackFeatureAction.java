package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import gitflow.GitflowConfigUtil;
import gitflow.ui.GitflowBranchChooseDialog;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

public class TrackFeatureAction extends AbstractTrackAction {

    TrackFeatureAction(){
        super("Track Feature", BranchType.Feature);
    }

    TrackFeatureAction(GitRepository repo){
        super(repo,"Track Feature", BranchType.Feature);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        ArrayList<String> remoteBranches = branchUtil.getRemoteBranchNames();
        ArrayList<String> remoteFeatureBranches = new ArrayList<String>();

        //get only the branches with the proper prefix
        for(Iterator<String> i = remoteBranches.iterator(); i.hasNext(); ) {
            String item = i.next();
            if (item.contains(featurePrefix)){
                remoteFeatureBranches.add(item);
            }
        }

        if (remoteBranches.size()>0){
            GitflowBranchChooseDialog branchChoose = new GitflowBranchChooseDialog(myProject,remoteFeatureBranches);

            branchChoose.show();
            if (branchChoose.isOK()){
                String branchName= branchChoose.getSelectedBranchName();
                final String featureName= GitflowConfigUtil.getFeatureNameFromBranch(myProject, myRepo, branchName);
                final GitRemote remote=branchUtil.getRemoteByBranch(branchName);
                final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);

                new Task.Backgroundable(myProject,"Tracking feature "+featureName,false){
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        GitCommandResult result = myGitflow.trackFeature(myRepo, featureName, remote, errorLineHandler);

                        if (result.success()) {
                            String trackedFeatureMessage = String.format("A new branch '%s%s' was created", featurePrefix, featureName);
                            NotifyUtil.notifySuccess(myProject, featureName, trackedFeatureMessage);
                        }
                        else {
                            NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                        }

                        myRepo.update();

                    }
                }.queue();
            }
        }
        else {
            NotifyUtil.notifyError(myProject, "Error", "No remote branches");
        }

    }
}