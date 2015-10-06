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

public class FinishHotfixAction extends GitFlowAVHAction {

    public FinishHotfixAction() {
        super("Finish Hotfix");
    }

    /**
     * @param e AnActionEvent
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        String currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);

        if (!currentBranchName.isEmpty()){

            //TODO HOTFIX NAME
            final String hotfixName = GitFlowAVHConfigUtil.getHotfixNameFromBranch(myProject, currentBranchName);

            final String tagMessage;

            String defaultTagMessage= GitFlowAVHConfigurable.getCustomHotfixTagCommitMessage(myProject);
            defaultTagMessage=defaultTagMessage.replace("%name%", hotfixName);

            if (GitFlowAVHConfigurable.dontTagHotfix(myProject)) {
                tagMessage="";
            }
            else {
                tagMessage = Messages.showInputDialog(myProject, "Enter the tag message:", "Finish Hotfix", Messages.getQuestionIcon(), defaultTagMessage, null);
            }

            final GitFlowAVHErrorsListener errorLineHandler = new GitFlowAVHErrorsListener(myProject);

            if (tagMessage!=null){
                new Task.Backgroundable(myProject,"Finishing hotfix "+hotfixName,false){
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        GitCommandResult result=  myGitflow.finishHotfix(repo, hotfixName, tagMessage, errorLineHandler);

                        if (result.success()) {
                            String finishedHotfixMessage = String.format("The hotfix branch '%s%s' was merged into '%s' and '%s'", hotfixPrefix, hotfixName, developBranch, masterBranch);
                            NotifyUtil.notifySuccess(myProject, hotfixName, finishedHotfixMessage);
                        }
                        else {
                            NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                        }

                        repo.update();

                    }
                }.queue();
            }
        }

    }

}