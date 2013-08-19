package git4idea.commands;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import git4idea.GitUtil;
import git4idea.GitVcs;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import git4idea.ui.GitflowBranchChooseDialog;
import git4idea.ui.branch.GitMultiRootBranchConfig;
import git4idea.validators.GitNewBranchNameValidator;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Opher
 * Date: 18/08/13
 * Time: 22:38


 All actions associated with gitflow
 */
public class GitflowActions {
    Project myProject;

    public GitflowActions(@NotNull Project project){
        myProject=project;
    }


    public ActionGroup getActions(){
        DefaultActionGroup actionGroup= new DefaultActionGroup(null, false);
        actionGroup.add(new InitRepo(myProject));
        actionGroup.addSeparator();
        actionGroup.add(new StartFeatureAction(myProject));
        actionGroup.add(new FinishFeatureAction(myProject));
        actionGroup.addSeparator();
        actionGroup.add(new PullFeatureAction(myProject));

        return actionGroup;
    }

    private static class InitRepo extends DumbAwareAction {
        Gitflow myGitflow = ServiceManager.getService(Gitflow.class);
        private final Project myProject;
        GitRepository repo;
        ArrayList<GitRepository> repos = new ArrayList<GitRepository>();

        InitRepo(@NotNull Project project) {
            super("Init Repo");
            myProject = project;

            repo = GitBranchUtil.getCurrentRepository(myProject);
            repos.add(repo);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            repo = GitBranchUtil.getCurrentRepository(myProject);
            repos.add(repo);

            GitCommandResult res;
            res=  myGitflow.initRepo(repo, new gitFlowErrorsListener().init(myProject),
                                           new lineHandler().init(myProject));

        }

        private class lineHandler extends gitflowLineHandler{
            @Override
            public void onLineAvailable(String line, Key outputType) {
                if (line.contains("Already initialized for gitflow")){
                    new Notification(GitVcs.IMPORTANT_ERROR_NOTIFICATION.getDisplayId(), "Error", "Repo already initialized for gitflow", NotificationType.WARNING).notify(myProject);
                }

            }
        }

    }

    private static class StartFeatureAction extends DumbAwareAction {
        Gitflow myGitflow = ServiceManager.getService(Gitflow.class);
        private final Project myProject;
        GitRepository repo;
        ArrayList<GitRepository> repos = new ArrayList<GitRepository>();

        StartFeatureAction(@NotNull Project project) {
            super("Start Feature");
            myProject = project;

            repo = GitBranchUtil.getCurrentRepository(myProject);
            repos.add(repo);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            GitCommandResult res;

            String featureName = Messages.showInputDialog(myProject, "Enter the name of new feature:", "New Feature", Messages.getQuestionIcon(), "",
                    GitNewBranchNameValidator.newInstance(repos));

            if (featureName!=null){
                res=  myGitflow.startFeature(repo,featureName,new gitFlowErrorsListener().init(myProject) );
            }


            //TODO update git status bar widget
        }

    }

    private static class FinishFeatureAction extends DumbAwareAction {
        Gitflow myGitflow = ServiceManager.getService(Gitflow.class);
        private final Project myProject;

        FinishFeatureAction(@NotNull Project project) {
            super("Finish Feature");
            myProject = project;
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            GitCommandResult res;
            GitRepository repo = GitBranchUtil.getCurrentRepository(myProject);
            String currentBranch = GitBranchUtil.getBranchNameOrRev(repo);
            if (currentBranch.isEmpty()==false){
                //TODO currently relies on defaults, should read from config
                String featureName = currentBranch.replace("feature/","");
                res=  myGitflow.finishFeature(repo,featureName,new gitFlowErrorsListener().init(myProject) );
            }


            //TODO update git status bar widget
        }

    }

    private static class PullFeatureAction extends DumbAwareAction{
        private final Project myProject;
        Gitflow myGitflow = ServiceManager.getService(Gitflow.class);
        GitRepositoryManager myRepositoryManager;
        GitMultiRootBranchConfig myMultiRootBranchConfig;

        PullFeatureAction(@NotNull Project project){
            super("Pull Feature");
            myProject = project;
            GitRepositoryManager myRepositoryManager = GitUtil.getRepositoryManager(myProject);
            myMultiRootBranchConfig = new GitMultiRootBranchConfig(myRepositoryManager.getRepositories());
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

            ArrayList<String> remoteBranches = new ArrayList<String>(myMultiRootBranchConfig.getRemoteBranches());
            if (remoteBranches.isEmpty()){
                GitflowBranchChooseDialog branchChoose = new GitflowBranchChooseDialog(myProject,remoteBranches);
                branchChoose.show();
            }
            else{
                new Notification(GitVcs.IMPORTANT_ERROR_NOTIFICATION.getDisplayId(), "Error", "No remote branches", NotificationType.ERROR).notify(myProject);
            }

        }
    }


    private static class gitFlowErrorsListener extends gitflowLineHandler{

        @Override
        public void onLineAvailable(String line, Key outputType) {
            if (line.contains("'flow' is not a git command")){
                new Notification(GitVcs.IMPORTANT_ERROR_NOTIFICATION.getDisplayId(), "Error", "Gitflow is not installed", NotificationType.ERROR).notify(myProject);
            }
            if (line.contains("Not a gitflow-enabled repo yet")){
                new Notification(GitVcs.IMPORTANT_ERROR_NOTIFICATION.getDisplayId(), "Error", "Not a gitflow-enabled repo yet. Please init git flow", NotificationType.ERROR).notify(myProject);
            }
        }

    };

    private static abstract class gitflowLineHandler implements GitLineHandlerListener{
        Project myProject;

        public GitLineHandlerListener init(@NotNull Project project){
            myProject=project;
            return this;
        }

        @Override
        public void onLineAvailable(String line, Key outputType) {}

        @Override
        public void processTerminated(int exitCode) {}

        @Override
        public void startFailed(Throwable exception) {}
    }
}
