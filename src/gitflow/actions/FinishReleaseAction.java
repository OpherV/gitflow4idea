package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import gitflow.GitflowConfigUtil;
import gitflow.GitflowConfigurable;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class FinishReleaseAction extends GitflowAction {

    FinishReleaseAction() {
        super("Finish Release");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        String currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);
        if (currentBranchName.isEmpty()==false){

            final String releaseName = GitflowConfigUtil.getReleaseNameFromBranch(myProject, currentBranchName);
            final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);
            String defaultTagMessage= GitflowConfigurable.getCustomTagCommitMessage(myProject);
            defaultTagMessage=defaultTagMessage.replace("%name%", releaseName);

            String tagMessageDraft;
            final String tagMessage;

            boolean cancelAction=false;

            if (GitflowConfigurable.dontTagRelease(myProject)) {
                tagMessage="";
            }
            else{
                tagMessageDraft= Messages.showInputDialog(myProject, "Enter the tag message:", "Finish Release", Messages.getQuestionIcon(), defaultTagMessage, null);
                if (tagMessageDraft==null){
                    cancelAction=true;
                    tagMessage="";
                }
                else{

                    tagMessage=tagMessageDraft;
                }
            }


            if (!cancelAction){

                new Task.Backgroundable(myProject,"Finishing release "+releaseName,false){
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        GitCommandResult result =  myGitflow.finishRelease(repo, releaseName, tagMessage, errorLineHandler);

                        if (result.success()) {
                            String finishedReleaseMessage = String.format("The release branch '%s%s' was merged into '%s' and '%s'", featurePrefix, releaseName, developBranch, masterBranch);
                            NotifyUtil.notifySuccess(myProject, releaseName, finishedReleaseMessage);
                        }
                        else {
                            NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                        }



                    }

                    @Override
                    public void onSuccess() {
                        super.onSuccess();

                        virtualFileMananger.syncRefresh();
                        repo.update();
                    }

                }.queue();

            }
        }

    }

}