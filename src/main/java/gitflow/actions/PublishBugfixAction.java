package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import gitflow.GitflowConfigUtil;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class PublishBugfixAction extends AbstractPublishAction {

    PublishBugfixAction(){
        super("Publish Bugfix", BranchType.Bugfix);
    }

    PublishBugfixAction(GitRepository repo){
        super(repo, "Publish Bugfix", BranchType.Bugfix);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        GitflowConfigUtil gitflowConfigUtil = GitflowConfigUtil.getInstance(myProject, myRepo);
        final String bugfixName = gitflowConfigUtil.getBugfixNameFromBranch(branchUtil.getCurrentBranchName());

        new Task.Backgroundable(myProject,"Publishing bugfix "+bugfixName,false){
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitCommandResult result = myGitflow.publishBugfix(myRepo, bugfixName,new GitflowErrorsListener(myProject));
                if (result.success()) {
                    String publishedBugfixMessage = String.format("A new remote branch '%s%s' was created", branchUtil.getPrefixBugfix(), bugfixName);
                    NotifyUtil.notifySuccess(myProject, bugfixName, publishedBugfixMessage);
                }
                else {
                    NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                }
                myRepo.update();
            }
        }.queue();
    }

}