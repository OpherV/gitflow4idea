package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import gitflow.GitflowConfigUtil;
import gitflow.GitflowConfigurable;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class FinishReleaseAction extends AbstractBranchAction {

	String customReleaseName=null;
	String customtagMessage=null;

    FinishReleaseAction() {
        super("Finish Release", AbstractBranchAction.BranchType.Release);
    }

    FinishReleaseAction(GitRepository repo) {
        super(repo, "Finish Release", BranchType.Release);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);
        finishRelease(customReleaseName, customtagMessage);
    }

    private void finishRelease(final String customName, final String customMessage) {
        String currentBranchName = GitBranchUtil.getBranchNameOrRev(myRepo);
        if (currentBranchName.isEmpty()) {
            return;
        }

        final String tagMessage;
        final String releaseName;

        GitflowConfigUtil gitflowConfigUtil = GitflowConfigUtil.getInstance(myProject, myRepo);

        // Check if a release name was specified, otherwise take name from current branch
        releaseName = customName != null ? customName : gitflowConfigUtil.getReleaseNameFromBranch(currentBranchName);

        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);

        String tagMessageTemplate = GitflowConfigurable.getOptionTextString(myProject, "RELEASE_customTagCommitMessage").replace("%name%", releaseName);
        String tagMessageDraft;

        boolean cancelAction = false;

        if (GitflowConfigurable.isOptionActive(myProject, "RELEASE_dontTag")) {
            tagMessage = "";
        } else if (customMessage != null) {
            //probably repeating the release finish after a merge
            tagMessage = customMessage;
        } else {
            tagMessageDraft = Messages.showInputDialog(myProject, "Enter the tag message:", "Finish Release", Messages.getQuestionIcon(), tagMessageTemplate, null);
            if (tagMessageDraft == null) {
                cancelAction = true;
                tagMessage = "";
            } else {
                tagMessage = tagMessageDraft;
            }
        }

        if (!cancelAction) {

            new Task.Backgroundable(myProject, "Finishing release " + releaseName, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitCommandResult result = myGitflow.finishRelease(myRepo, releaseName, tagMessage, errorLineHandler);

                    if (result.success()) {
                        String finishedReleaseMessage = String.format("The release branch '%s%s' was merged into '%s' and '%s'", branchUtil.getPrefixRelease(), releaseName, branchUtil.getBranchnameDevelop(), branchUtil.getBranchnameMaster());
                        NotifyUtil.notifySuccess(myProject, releaseName, finishedReleaseMessage);
                    } else if (errorLineHandler.hasMergeError) {
                        // (merge errors are handled in the onSuccess handler)
                    } else {
                        NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                    }

                    myRepo.update();
                }

                @Override
                public void onSuccess() {
                    super.onSuccess();

                    //merge conflicts if necessary
                    if (errorLineHandler.hasMergeError) {
                        if (handleMerge(myProject)) {
                            // Repeat the release finish with the already resolved tag message.
                            finishRelease(releaseName, tagMessage);
                        }
                    }
                }

            }.queue();

        }
    }
}
