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

public class FinishReleaseAction extends GitflowAction {

	String customReleaseName=null;
	String customtagMessage=null;

    FinishReleaseAction() {
        super("Finish Release");
    }

    FinishReleaseAction(GitRepository repo) {
        super(repo, "Finish Release");
    }

	FinishReleaseAction(String name, String tagMessage) {
		super("Finish Release");
		customReleaseName=name;
		customtagMessage=tagMessage;
	}

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        String currentBranchName = GitBranchUtil.getBranchNameOrRev(myRepo);
        if (currentBranchName.isEmpty()==false){

	        final AnActionEvent event=e;

            final String tagMessage;
            final String releaseName;

	        // Check if a release name was specified, otherwise take name from current branch
	        releaseName = customReleaseName!=null ? customReleaseName:GitflowConfigUtil.getReleaseNameFromBranch(myProject, myRepo, currentBranchName);

            final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);
            String defaultTagMessage= GitflowConfigurable.getCustomTagCommitMessage(myProject);
            defaultTagMessage=defaultTagMessage.replace("%name%", releaseName);

            String tagMessageDraft;

            boolean cancelAction=false;

            if (GitflowConfigurable.dontTagRelease(myProject)) {
                tagMessage="";
            }
            else if (customtagMessage!=null){
	            //probably repeating the release finish after a merge
	            tagMessage=customtagMessage;
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
                        GitCommandResult result =  myGitflow.finishRelease(myRepo, releaseName, tagMessage, errorLineHandler);

                        if (result.success()) {
                            String finishedReleaseMessage = String.format("The release branch '%s%s' was merged into '%s' and '%s'", releasePrefix, releaseName, developBranch, masterBranch);
                            NotifyUtil.notifySuccess(myProject, releaseName, finishedReleaseMessage);
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
		                    if (handleMerge()) {
			                    FinishReleaseAction completeFinisReleaseAction = new FinishReleaseAction(releaseName,tagMessage);
			                    completeFinisReleaseAction.actionPerformed(event);
		                    }
	                    }
                    }

                }.queue();

            }
        }

    }

}