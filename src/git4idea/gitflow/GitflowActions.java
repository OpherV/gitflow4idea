package git4idea.gitflow;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import git4idea.GitUtil;
import git4idea.GitVcs;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitLineHandlerListener;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import git4idea.gitflow.ui.GitflowBranchChooseDialog;
import git4idea.ui.branch.GitMultiRootBranchConfig;
import git4idea.validators.GitNewBranchNameValidator;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.Iterator;

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
        GitRepository repo = GitBranchUtil.getCurrentRepository(myProject);


        DefaultActionGroup actionGroup= new DefaultActionGroup(null, false);

        //gitflow not setup
        if (BranchUtil.hasGitflow(myProject)!=true){
            actionGroup.add(new InitRepo(myProject));
        }
        else{
            actionGroup.add(new StartFeatureAction(myProject));

            //feature only actions
            if (BranchUtil.isCurrentbranchFeature(myProject)){
                actionGroup.add(new FinishFeatureAction(myProject));
                actionGroup.add(new PublishFeatureAction().init(myProject));
            }
            actionGroup.addSeparator();

            actionGroup.add(new PullFeatureAction().init(myProject));
        }
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

    private static class StartFeatureAction extends GitFlowAction {
        ArrayList<GitRepository> repos = new ArrayList<GitRepository>();

        StartFeatureAction(@NotNull Project project) {
            super("Start Feature");
            repos.add(repo);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

            String featureName = Messages.showInputDialog(myProject, "Enter the name of new feature:", "New Feature", Messages.getQuestionIcon(), "",
                    GitNewBranchNameValidator.newInstance(repos));

            if (featureName!=null){
                runAsync("Starting new feature",featureName);
            }

        }

        @Override
        protected void asyncTask() {
            GitCommandResult res=  myGitflow.startFeature(repo,featureName,new gitFlowErrorsListener().init(myProject) );
            repo.update();
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
            String currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);
            if (currentBranchName.isEmpty()==false){

                String featureName = ConfigUtil.getFeatureNameFromBranch(myProject, currentBranchName);
                res=  myGitflow.finishFeature(repo,featureName,new gitFlowErrorsListener().init(myProject) );
                repo.update();
            }

        }

    }

    private static class PublishFeatureAction extends GitFlowAction{
        PublishFeatureAction(){
            super("Publish Feature");
        }

        @Override
        public void actionPerformed(AnActionEvent anActionEvent) {
            String featureName=ConfigUtil.getFeatureNameFromBranch(myProject,currentBranchName);
            myGitflow.publishFeature(repo,featureName,new gitFlowErrorsListener().init(myProject));
        }
    }

    private static class PullFeatureAction extends GitFlowAction{

        PullFeatureAction(){
            super("Pull Feature");
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

            String featurePrefix = ConfigUtil.getFeaturePrefix(myProject);

            ArrayList<String> remoteBranches = new ArrayList<String>(myMultiRootBranchConfig.getRemoteBranches());
            ArrayList<String> remoteFeatureBranches = new ArrayList<String>();

            //get only the branches with the proper prefix
            for(Iterator<String> i = remoteBranches.iterator(); i.hasNext(); ) {
                String item = i.next();
                if (item.contains(featurePrefix)){
                    remoteFeatureBranches.add(item);
                }
            }

            if (remoteBranches.size()>0){
                GitflowBranchChooseDialog branchChoose = new GitflowBranchChooseDialog(myProject,remoteFeatureBranches);

                branchChoose.show();
                if (branchChoose.isOK()){
                    String branchName= branchChoose.getSelectedBranchName();
                    String featureName=ConfigUtil.getFeatureNameFromBranch(myProject,branchName);
                    String remoteName=ConfigUtil.getRemoteNameFromBranch(myProject,branchName);
                    myGitflow.pullFeature(repo,featureName, remoteName, new gitFlowErrorsListener().init(myProject));
                }
            }
            else{
                new Notification(GitVcs.IMPORTANT_ERROR_NOTIFICATION.getDisplayId(), "Error", "No remote branches", NotificationType.ERROR).notify(myProject);
            }

        }
    }


    private static abstract class GitFlowAction extends DumbAwareAction{
        protected Project myProject;
        protected Gitflow myGitflow = ServiceManager.getService(Gitflow.class);
        protected GitRepositoryManager myRepositoryManager;
        protected GitMultiRootBranchConfig myMultiRootBranchConfig;
        protected GitRepository repo;
        protected String currentBranchName;

        GitFlowAction(String featureName){
            super(featureName);
        }

        protected GitFlowAction init(@NotNull Project project){
            myProject = project;
            myRepositoryManager = GitUtil.getRepositoryManager(myProject);
            myMultiRootBranchConfig = new GitMultiRootBranchConfig(myRepositoryManager.getRepositories());
            repo = GitBranchUtil.getCurrentRepository(myProject);
            currentBranchName= GitBranchUtil.getBranchNameOrRev(repo);

            return this;
        }

        protected void asyncTask(){}

        protected void runAsync(String title){
            new Task.Backgroundable(myProject,title,false){
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    asyncTask();
                }
            }.queue();
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

    private static abstract class gitflowLineHandler implements GitLineHandlerListener {
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
