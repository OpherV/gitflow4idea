package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import git4idea.validators.GitNewBranchNameValidator;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class StartReleaseAction extends AbstractStartAction {

    StartReleaseAction() {
        super("Start Release");
    }

    StartReleaseAction(GitRepository repo) {
        super(repo,"Start Release");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        final String releaseName = Messages.showInputDialog(myProject, "Enter the name of new release:", "New Release", Messages.getQuestionIcon(), "",
                GitNewBranchNameValidator.newInstance(repos));
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);

        if (releaseName == null){
            // user clicked cancel
        }
        else if (releaseName.isEmpty()){
            Messages.showWarningDialog(myProject, "You must provide a name for the release", "Whoops");
        }
        else {
            new Task.Backgroundable(myProject,"Starting release "+releaseName,false){
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitCommandResult result=  myGitflow.startRelease(myRepo, releaseName, errorLineHandler);

                    if (result.success()) {
                        String startedReleaseMessage = String.format("A new release '%s%s' was created, based on '%s'", releasePrefix, releaseName, developBranch);
                        NotifyUtil.notifySuccess(myProject, releaseName, startedReleaseMessage);
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