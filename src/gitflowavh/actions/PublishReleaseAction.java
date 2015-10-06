package gitflowavh.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.commands.GitCommandResult;
import gitflowavh.GitFlowAVHConfigUtil;
import gitflowavh.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class PublishReleaseAction extends GitFlowAVHAction {

    PublishReleaseAction(){
        super("Publish Release");
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        final String releaseName= GitFlowAVHConfigUtil.getReleaseNameFromBranch(myProject, currentBranchName);
        final GitFlowAVHErrorsListener errorLineHandler = new GitFlowAVHErrorsListener(myProject);

        new Task.Backgroundable(myProject,"Publishing release "+releaseName,false){
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitCommandResult result = myGitflow.publishRelease(repo, releaseName, errorLineHandler);

                if (result.success()) {
                    String publishedReleaseMessage = String.format("A new remote branch '%s%s' was created", releasePrefix, releaseName);
                    NotifyUtil.notifySuccess(myProject, releaseName, publishedReleaseMessage);
                }
                else {
                    NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                }

                repo.update();
            }
        }.queue();

    }
}