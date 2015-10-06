package gitflowavh.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import gitflowavh.GitFlowAVHConfigUtil;
import gitflowavh.GitFlowAVHConfigurable;
import gitflowavh.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class FinishReleaseAction extends GitFlowAVHAction {

    String customReleaseName = null;
    String customtagMessage = null;

    FinishReleaseAction() {
        super("Finish Release");
    }

    FinishReleaseAction(String name, String tagMessage) {
        super("Finish Release");
        customReleaseName = name;
        customtagMessage = tagMessage;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        String currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);
        if (!currentBranchName.isEmpty()) {

            final AnActionEvent event = e;

            final String tagMessage;
            final String releaseName;

            // Check if a release name was specified, otherwise take name from current branch
            releaseName = customReleaseName != null ? customReleaseName : GitFlowAVHConfigUtil.getReleaseNameFromBranch(myProject, currentBranchName);

            final GitFlowAVHErrorsListener errorLineHandler = new GitFlowAVHErrorsListener(myProject);
            String defaultTagMessage = GitFlowAVHConfigurable.getCustomTagCommitMessage(myProject);
            defaultTagMessage = defaultTagMessage.replace("%name%", releaseName);

            String tagMessageDraft;

            boolean cancelAction = false;

            if (GitFlowAVHConfigurable.dontTagRelease(myProject)) {
                tagMessage = "";
            } else if (customtagMessage != null) {
                //probably repeating the release finish after a merge
                tagMessage = customtagMessage;
            } else {
                tagMessageDraft = Messages.showInputDialog(myProject, "Enter the tag message:", "Finish Release", Messages.getQuestionIcon(), defaultTagMessage, null);
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
                        GitCommandResult result = myGitflow.finishRelease(repo, releaseName, tagMessage, errorLineHandler);

                        if (result.success()) {
                            String finishedReleaseMessage = String.format("The release branch '%s%s' was merged into '%s' and '%s'", featurePrefix, releaseName, developBranch, masterBranch);
                            NotifyUtil.notifySuccess(myProject, releaseName, finishedReleaseMessage);
                        } else {
                            if (!errorLineHandler.hasMergeError) {
                                NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                            }
                        }

                        repo.update();

                    }

                    @Override
                    public void onSuccess() {
                        super.onSuccess();

                        // Merge conflicts if necessary
                        if (errorLineHandler.hasMergeError) {
                            if (handleMerge()) {
                                FinishReleaseAction completeFinisReleaseAction = new FinishReleaseAction(releaseName, tagMessage);
                                completeFinisReleaseAction.actionPerformed(event);
                            }
                        }
                    }

                }.queue();

            }
        }

    }

}