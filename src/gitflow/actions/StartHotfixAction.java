package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import git4idea.commands.GitCommandResult;
import git4idea.validators.GitNewBranchNameValidator;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class StartHotfixAction extends GitflowAction {

    StartHotfixAction() {
        super("Start Hotfix");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        final String hotfixName = Messages.showInputDialog(myProject, "Enter the name of the new hotfix:", "New Hotfix", Messages.getQuestionIcon(), "",
                GitNewBranchNameValidator.newInstance(repos));
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);

        //must insert hotfix name
        if (hotfixName!=null && !hotfixName.isEmpty()){
            new Task.Backgroundable(myProject,"Starting hotfix "+hotfixName,false){
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitCommandResult result =  myGitflow.startHotfix(repo, hotfixName, errorLineHandler);

                    if (result.success()) {
                        String startedHotfixMessage = String.format("A new hotfix '%s%s' was created, based on '%s'", hotfixPrefix, hotfixName, masterBranch);
                        NotifyUtil.notifySuccess(myProject, hotfixName, startedHotfixMessage);
                    }
                    else {
                        NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                    }

                    repo.update();

                }
            }.queue();

        }
        else{
            Messages.showWarningDialog(myProject, "You must provide a name for the hotfix", "Whoops");
        }

    }
}