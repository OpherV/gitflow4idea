package gitflow.actions;

import com.intellij.dvcs.repo.Repository;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import git4idea.commands.GitCommandResult;
import gitflow.ui.GitflowStartFeatureDialog;
import gitflow.ui.NotifyUtil;

public class StartFeatureAction extends GitflowAction {

    public StartFeatureAction() {
        super("Start Feature");
    }
    public StartFeatureAction(GitRepository repo) {
        super(repo, "Start Feature");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        GitflowStartFeatureDialog dialog = new GitflowStartFeatureDialog(myProject, myRepo);
        dialog.show();

        if (dialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) return;

        final String featureName = dialog.getNewBranchName();
        final String baseBranchName = dialog.getBaseBranchName();

        this.runAction(e.getProject(), baseBranchName, featureName);
    }

    public void runAction(Project project, final String baseBranchName, final String featureName){
        super.runAction(project, baseBranchName, featureName);

        new Task.Backgroundable(myProject, "Starting feature " + featureName, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                createFeatureBranch(baseBranchName, featureName);
            }
        }.queue();
    }

    private void createFeatureBranch(String baseBranchName, String featureName) {
        GitflowErrorsListener errorListener = new GitflowErrorsListener(myProject);
        GitCommandResult result = myGitflow.startFeature(myRepo, featureName, baseBranchName, errorListener);

        if (result.success()) {
            String startedFeatureMessage = String.format("A new branch '%s%s' was created, based on '%s'", featurePrefix, featureName, baseBranchName);
            NotifyUtil.notifySuccess(myProject, featureName, startedFeatureMessage);
        } else {
            NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
        }

        myRepo.update();
        virtualFileMananger.asyncRefresh(null); //update editors
    }
}