package gitflowavh.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.util.Key;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import gitflowavh.GitFlowAVHInitOptions;
import gitflowavh.ui.GitFlowAVHInitOptionsDialog;
import gitflowavh.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class InitRepoAction extends GitFlowAVHAction {

    public InitRepoAction() {
        super("Init Repo");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        GitFlowAVHInitOptionsDialog optionsDialog = new GitFlowAVHInitOptionsDialog(myProject, branchUtil.getLocalBranchNames());
        optionsDialog.show();

        if(optionsDialog.isOK()) {
            final GitFlowAVHErrorsListener errorLineHandler = new GitFlowAVHErrorsListener(myProject);
            final LineHandler localLineHandler = new LineHandler();
            final GitFlowAVHInitOptions initOptions = optionsDialog.getOptions();

            new Task.Backgroundable(myProject,"Initializing repo",false){
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitCommandResult result = myGitflow.initRepo(repo, initOptions, errorLineHandler, localLineHandler);

                    if (result.success()) {
                        String publishedFeatureMessage = "Initialized gitflow repo";
                        NotifyUtil.notifySuccess(myProject, "", publishedFeatureMessage);
                    } else {
                        NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                    }

                    //update the widget
                    myProject.getMessageBus().syncPublisher(GitRepository.GIT_REPO_CHANGE).repositoryChanged(repo);
                    repo.update();
                }
            }.queue();
        }

    }

    private class LineHandler extends GitFlowAVHLineHandler {
        @Override
        public void onLineAvailable(String line, Key outputType) {
            if (line.contains("Already initialized for gitflow")){
                myErrors.add("Repo already initialized for gitflow");
            }

        }
    }

}