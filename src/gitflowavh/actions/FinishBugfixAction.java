package gitflowavh.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import gitflowavh.GitFlowAVHConfigUtil;
import gitflowavh.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class FinishBugfixAction extends GitFlowAVHAction {

    String customBugfixName = null;

    public FinishBugfixAction() {
        super("Finish Bugfix");
    }

    public FinishBugfixAction(String name) {
        super("Finish Bugfix");
        customBugfixName = name;
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
            final String bugfixName;
            // Check if a bugfix name was specified, otherwise take name from current branch
            if (customBugfixName != null) {
                bugfixName = customBugfixName;
            } else {
                bugfixName = GitFlowAVHConfigUtil.getBugfixNameFromBranch(myProject, currentBranchName);
            }
            final GitFlowAVHErrorsListener errorLineHandler = new GitFlowAVHErrorsListener(myProject);

            new Task.Backgroundable(myProject, "Finishing bugfix " + bugfixName, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitCommandResult result = myGitflow.finishBugfix(repo, bugfixName, errorLineHandler);


                    if (result.success()) {
                        String finishedBugfixMessage = String.format("The bugfix branch '%s%s' was merged into '%s'", bugfixPrefix, bugfixName, developBranch);
                        NotifyUtil.notifySuccess(myProject, bugfixName, finishedBugfixMessage);
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
                            FinishBugfixAction completeFinishBugfixAction = new FinishBugfixAction(bugfixName);
                            completeFinishBugfixAction.actionPerformed(event);
                        }

                    }

                }
            }.queue();
        }

    }

}