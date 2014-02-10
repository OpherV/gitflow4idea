package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.Key;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import git4idea.util.GitUIUtil;
import gitflow.GitflowInitOptions;
import gitflow.actions.GitflowAction;
import gitflow.actions.GitflowErrorsListener;
import gitflow.actions.GitflowLineHandler;
import gitflow.ui.GitflowInitOptionsDialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class InitRepoAction extends GitflowAction {

    InitRepoAction() {
        super("Init Repo");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        GitflowInitOptionsDialog optionsDialog = new GitflowInitOptionsDialog(myProject, branchUtil.getLocalBranchNames());
        optionsDialog.show();

        if(optionsDialog.isOK()) {
            final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);
            final LineHandler localLineHandler = new LineHandler();
            final GitflowInitOptions initOptions = optionsDialog.getOptions();

            new Task.Backgroundable(myProject,"Initializing repo",false){
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitCommandResult result = myGitflow.initRepo(repo, initOptions, errorLineHandler, localLineHandler);

                    if (result.success()) {
                        String publishedFeatureMessage = String.format("Initialized gitflow repo");
                        GitUIUtil.notifySuccess(myProject, publishedFeatureMessage, "");
                    } else {
                        GitUIUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                    }

                    repo.update();
                }
            }.queue();
        }

    }

    private class LineHandler extends GitflowLineHandler {
        @Override
        public void onLineAvailable(String line, Key outputType) {
            if (line.contains("Already initialized for gitflow")){
                myErrors.add("Repo already initialized for gitflow");
            }

        }
    }

}