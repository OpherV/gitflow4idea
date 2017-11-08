package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import gitflow.GitflowConfigUtil;
import gitflow.GitflowConfigurable;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class FinishHotfixAction extends GitflowAction {

    public FinishHotfixAction() {
        super("Finish Hotfix");
    }
    public FinishHotfixAction(GitRepository repo) {
        super(repo, "Finish Hotfix");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        String currentBranchName = GitBranchUtil.getBranchNameOrRev(myRepo);


        if (currentBranchName.isEmpty()==false){

            //TODO HOTFIX NAME
            final String hotfixName = GitflowConfigUtil.getHotfixNameFromBranch(myProject, myRepo, currentBranchName);

            final String tagMessage;

            String defaultTagMessage= GitflowConfigurable.getCustomHotfixTagCommitMessage(myProject);
            defaultTagMessage=defaultTagMessage.replace("%name%", hotfixName);

            if (GitflowConfigurable.dontTagHotfix(myProject)) {
                tagMessage="";
            }
            else {
                tagMessage = Messages.showInputDialog(myProject, "Enter the tag message:", "Finish Hotfix", Messages.getQuestionIcon(), defaultTagMessage, null);
            }

            this.runAction(e.getProject(), hotfixName, tagMessage);

        }

    }

    public void runAction(final Project project, final String hotfixName, final String tagMessage){
        super.runAction(project, null, hotfixName);

        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);

        if (tagMessage!=null){
            new Task.Backgroundable(myProject,"Finishing hotfix "+hotfixName,false){
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitCommandResult result=  myGitflow.finishHotfix(myRepo, hotfixName, tagMessage, errorLineHandler);

                    if (result.success()) {
                        String finishedHotfixMessage = String.format("The hotfix branch '%s%s' was merged into '%s' and '%s'", hotfixPrefix, hotfixName, developBranch, masterBranch);
                        NotifyUtil.notifySuccess(myProject, hotfixName, finishedHotfixMessage);
                    }
                    else {
                        NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                    }

                    myRepo.update();

                }
            }.queue();
        }

    }

}