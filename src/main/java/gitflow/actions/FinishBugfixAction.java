package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import gitflow.GitflowConfigUtil;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class FinishBugfixAction extends AbstractBranchAction {

    String customBugfixName =null;

    public FinishBugfixAction() {
        super("Finish Bugfix", BranchType.Bugfix);
    }

    public FinishBugfixAction(GitRepository repo) {
        super(repo, "Finish Bugfix", BranchType.Bugfix);
    }

    FinishBugfixAction(GitRepository repo, String name) {
        super(repo, "Finish Bugfix", BranchType.Bugfix);
        customBugfixName =name;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        String currentBranchName = GitBranchUtil.getBranchNameOrRev(myRepo);
        if (currentBranchName.isEmpty()==false){

            final AnActionEvent event=e;
            final String bugfixName;
            // Check if a bugfix name was specified, otherwise take name from current branch
            if (customBugfixName !=null){
                bugfixName = customBugfixName;
            }
            else{
                GitflowConfigUtil gitflowConfigUtil = GitflowConfigUtil.getInstance(myProject, myRepo);
                bugfixName = gitflowConfigUtil.getBugfixNameFromBranch(currentBranchName);
            }

            this.runAction(myProject, bugfixName);
        }

    }

    public void runAction(final Project project, final String bugfixName){
        super.runAction(project, null, bugfixName, null);

        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);
        final FinishBugfixAction that = this;

        GitflowConfigUtil gitflowConfigUtil = GitflowConfigUtil.getInstance(project, myRepo);

        new Task.Backgroundable(myProject,"Finishing bugfix "+bugfixName,false){
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                //get the base branch for this bugfix
                final String baseBranch = gitflowConfigUtil.getBaseBranch(branchUtil.getPrefixBugfix()+bugfixName);

                GitCommandResult result =  myGitflow.finishBugfix(myRepo, bugfixName, errorLineHandler);

                if (result.success()) {
                    String finishedBugfixMessage = String.format("The bugfix branch '%s%s' was merged into '%s'", branchUtil.getPrefixBugfix(), bugfixName, baseBranch);
                    NotifyUtil.notifySuccess(myProject, bugfixName, finishedBugfixMessage);
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
                    if (handleMerge(project)){
                        that.runAction(project, bugfixName);
                        FinishBugfixAction completeFinishBugfixAction = new FinishBugfixAction(myRepo, bugfixName);
                    }

                }

            }
        }.queue();
    }

}
