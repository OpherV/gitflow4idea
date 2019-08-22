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

public class TrackBugfixAction extends AbstractTrackAction {

    TrackBugfixAction() {
        super("Track Bugfix", BranchType.Bugfix);
    }

    TrackBugfixAction(GitRepository repo) {
        super(repo, "Track Bugfix", BranchType.Bugfix);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        ArrayList<String> remoteBranches = branchUtil.getRemoteBranchNames();
        ArrayList<String> remoteBugfixBranches = new ArrayList<String>();

        //get only the branches with the proper prefix
        for (Iterator<String> i = remoteBranches.iterator(); i.hasNext(); ) {
            String item = i.next();
            if (item.contains(bugfixPrefix)) {
                remoteBugfixBranches.add(item);
            }
        }

        if (remoteBranches.size() > 0) {
            GitflowBranchChooseDialog branchChoose = new GitflowBranchChooseDialog(myProject, remoteBugfixBranches);

            branchChoose.show();
            if (branchChoose.isOK()) {
                String branchName = branchChoose.getSelectedBranchName();

                GitflowConfigUtil gitflowConfigUtil = GitflowConfigUtil.getInstance(myProject, myRepo);
                final String bugfixName = gitflowConfigUtil.getBugfixNameFromBranch(branchName);
                final GitRemote remote = branchUtil.getRemoteByBranch(branchName);
                final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);

                new Task.Backgroundable(myProject, "Tracking bugfix " + bugfixName, false) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        GitCommandResult result = myGitflow.trackBugfix(myRepo, bugfixName, remote, errorLineHandler);
                        if (result.success()) {
                            String trackedBugfixMessage = String.format("A new branch '%s%s' was created", bugfixPrefix, bugfixName);
                            NotifyUtil.notifySuccess(myProject, bugfixName, trackedBugfixMessage);
                        } else {
                            NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                        }
                        myRepo.update();
                    }
                }.queue();
            }
        } else {
            NotifyUtil.notifyError(myProject, "Error", "No remote branches");
        }

    }
}