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
import git4idea.util.GitUIUtil;
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
    Gitflow myGitflow = ServiceManager.getService(Gitflow.class);
    GitRepositoryManager myRepositoryManager;
    GitMultiRootBranchConfig myMultiRootBranchConfig;
    GitRepository repo;
    GitflowBranchUtil branchUtil;

    String currentBranchName;

    String featurePrefix;
    String releasePrefix;
    String masterBranch;
    String developBranch;

    boolean noRemoteTrackBranches;
    boolean noRemoteFeatureBranches;

    boolean pulledAllFeatureBranches;
    boolean pulledAllReleaseBranches;

    public GitflowActions(@NotNull Project project){
        myProject=project;

        branchUtil=new GitflowBranchUtil(project);

        myRepositoryManager = GitUtil.getRepositoryManager(myProject);
        myMultiRootBranchConfig = new GitMultiRootBranchConfig(myRepositoryManager.getRepositories());
        repo = GitBranchUtil.getCurrentRepository(myProject);
        if (repo!=null){
            currentBranchName= GitBranchUtil.getBranchNameOrRev(repo);
        }

        featurePrefix = GitflowConfigUtil.getFeaturePrefix(myProject);
        releasePrefix = GitflowConfigUtil.getReleasePrefix(myProject);
        masterBranch= GitflowConfigUtil.getMasterBranch(myProject);
        developBranch= GitflowConfigUtil.getDevelopBranch(myProject);

        noRemoteTrackBranches = branchUtil.getRemoteBranchesWithPrefix(releasePrefix).isEmpty();
        noRemoteFeatureBranches = branchUtil.getRemoteBranchesWithPrefix(featurePrefix).isEmpty();

        pulledAllFeatureBranches = branchUtil.areAllBranchesPulled(featurePrefix);
        pulledAllReleaseBranches = branchUtil.areAllBranchesPulled(releasePrefix);


    }

    public boolean hasGitflow(){
        return branchUtil.hasGitflow();
    }




    //constructs the actions for the widget popup
    public ActionGroup getActions(){


        DefaultActionGroup actionGroup= new DefaultActionGroup(null, false);

        //gitflow not setup
        if (branchUtil.hasGitflow()!=true){
            actionGroup.add(new InitRepo());
        }
        else{

            //FEATURE ACTIONS

            actionGroup.addSeparator("Feature");
            actionGroup.add(new StartFeatureAction());
            //feature only actions
            if (branchUtil.isCurrentBranchFeature()){
                actionGroup.add(new FinishFeatureAction());

                //can't publish feature if it's already published
                if (branchUtil.isCurrentBranchPublished()==false){
                    actionGroup.add(new PublishFeatureAction());
                }
            }

            //make sure there's a feature to pull, and that not all features are pulled
            if (noRemoteFeatureBranches ==false && pulledAllFeatureBranches==false){
                actionGroup.add(new PullFeatureAction());
            }


            //RELEASE ACTIONS

            actionGroup.addSeparator("Release");
            actionGroup.add(new StartReleaseAction());
            //feature only actions
            if (branchUtil.isCurrentBranchRelease()){
                actionGroup.add(new FinishReleaseAction());

                //can't publish release if it's already published
                if (branchUtil.isCurrentBranchPublished()==false){
                    actionGroup.add(new PublishReleaseAction());
                }
            }

            //make sure there's something to track and that not all features are pulled
            if (noRemoteTrackBranches==false  && pulledAllReleaseBranches==false){
                actionGroup.add(new TrackReleaseAction());
            }




        }
        return actionGroup;
    }

    private class InitRepo extends DumbAwareAction {
        ArrayList<GitRepository> repos = new ArrayList<GitRepository>();

        InitRepo() {
            super("Init Repo");
            repos.add(repo);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            repo = GitBranchUtil.getCurrentRepository(myProject);
            repos.add(repo);


            new Task.Backgroundable(myProject,"Initializing repo",false){
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitCommandResult res;
                    res=  myGitflow.initRepo(repo, new gitFlowErrorsListener(),
                            new lineHandler());

                    String publishedFeatureMessage = String.format("Initialized gitflow repo");

                    GitUIUtil.notifySuccess(myProject, publishedFeatureMessage,"");

                    repo.update();
                }
            }.queue();


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

    // feature actions

    private class StartFeatureAction extends DumbAwareAction {
        private ArrayList<GitRepository> repos = new ArrayList<GitRepository>();

        StartFeatureAction() {
            super("Start Feature");
            repos.add(repo);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

            final String featureName = Messages.showInputDialog(myProject, "Enter the name of new feature:", "New Feature", Messages.getQuestionIcon(), "",
                    GitNewBranchNameValidator.newInstance(repos));

            if (featureName!=null){
                    new Task.Backgroundable(myProject,"Starting feature "+featureName,false){
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            GitCommandResult res=  myGitflow.startFeature(repo,featureName,new gitFlowErrorsListener());
                            repo.update();

                            String startedFeatureMessage = String.format("A new branch '%s%s' was created, based on '%s'", featurePrefix, featureName, developBranch);
                            GitUIUtil.notifySuccess(myProject, featureName, startedFeatureMessage );
                        }
                    }.queue();

            }

        }
    }

    private class FinishFeatureAction extends DumbAwareAction{

        FinishFeatureAction() {
            super("Finish Feature");
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

            String currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);
            if (currentBranchName.isEmpty()==false){

                final String featureName = GitflowConfigUtil.getFeatureNameFromBranch(myProject, currentBranchName);

                new Task.Backgroundable(myProject,"Finishing feature "+featureName,false){
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        GitCommandResult res=  myGitflow.finishFeature(repo,featureName,new gitFlowErrorsListener());
                        repo.update();

                        String finishedFeatureMessage = String.format("The feature branch '%s%s' was merged into '%s'", featurePrefix, featureName, developBranch);

                        GitUIUtil.notifySuccess(myProject, featureName, finishedFeatureMessage);
                    }
                }.queue();
            }

        }

    }

    private class PublishFeatureAction extends DumbAwareAction{
        PublishFeatureAction(){
            super("Publish Feature");
        }

        @Override
        public void actionPerformed(AnActionEvent anActionEvent) {
            final String featureName= GitflowConfigUtil.getFeatureNameFromBranch(myProject, currentBranchName);


            new Task.Backgroundable(myProject,"Publishing feature "+featureName,false){
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    myGitflow.publishFeature(repo,featureName,new gitFlowErrorsListener());
                    repo.update();

                    String publishedFeatureMessage = String.format("A new remote branch '%s%s' was created", featurePrefix, featureName);

                    GitUIUtil.notifySuccess(myProject, featureName, publishedFeatureMessage);
                }
            }.queue();

        }
    }

    private class PullFeatureAction extends DumbAwareAction{

        PullFeatureAction(){
            super("Pull Feature");
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

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
                    final String featureName= GitflowConfigUtil.getFeatureNameFromBranch(myProject, branchName);
                    final String remoteName= GitflowConfigUtil.getRemoteNameFromBranch(myProject, branchName);


                    new Task.Backgroundable(myProject,"Pulling feature"+featureName,false){
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            myGitflow.pullFeature(repo,featureName, remoteName, new gitFlowErrorsListener());
                            repo.update();

                            String pulledFeatureMessage = String.format("A new branch '%s%s' was created", featurePrefix, featureName);

                            GitUIUtil.notifySuccess(myProject, featureName, pulledFeatureMessage);
                        }
                    }.queue();
                }
            }
            else{
                new Notification(GitVcs.IMPORTANT_ERROR_NOTIFICATION.getDisplayId(), "Error", "No remote branches", NotificationType.ERROR).notify(myProject);
            }

        }
    }


    //release actions

    private class StartReleaseAction extends DumbAwareAction {
        private ArrayList<GitRepository> repos = new ArrayList<GitRepository>();

        StartReleaseAction() {
            super("Start Release");
            repos.add(repo);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

            final String releaseName = Messages.showInputDialog(myProject, "Enter the name of new release:", "New Release", Messages.getQuestionIcon(), "",
                    GitNewBranchNameValidator.newInstance(repos));

            if (releaseName!=null){
                new Task.Backgroundable(myProject,"Starting release "+releaseName,false){
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        GitCommandResult res=  myGitflow.startRelease(repo, releaseName, new gitFlowErrorsListener());
                        repo.update();

                        String startedReleaseMessage = String.format("A new release '%s%s' was created, based on '%s'", releasePrefix, releaseName, developBranch);
                        GitUIUtil.notifySuccess(myProject, releaseName, startedReleaseMessage );
                    }
                }.queue();

            }

        }
    }

    private class FinishReleaseAction extends DumbAwareAction{

        FinishReleaseAction() {
            super("Finish Release");
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

            String currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);
            if (currentBranchName.isEmpty()==false){

                final String releaseName = GitflowConfigUtil.getFeatureNameFromBranch(myProject, currentBranchName);

                new Task.Backgroundable(myProject,"Finishing release "+releaseName,false){
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        GitCommandResult res=  myGitflow.finishRelease(repo, releaseName, new gitFlowErrorsListener());
                        repo.update();

                        String finishedReleaseMessage = String.format("The release branch '%s%s' was merged into '%s' and '%s'", featurePrefix, releaseName, developBranch, masterBranch);

                        GitUIUtil.notifySuccess(myProject, releaseName, finishedReleaseMessage);
                    }
                }.queue();
            }

        }

    }

    private class PublishReleaseAction extends DumbAwareAction{
        PublishReleaseAction(){
            super("Publish Release");
        }

        @Override
        public void actionPerformed(AnActionEvent anActionEvent) {
            final String releaseName= GitflowConfigUtil.getReleaseNameFromBranch(myProject, currentBranchName);


            new Task.Backgroundable(myProject,"Publishing release "+releaseName,false){
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    myGitflow.publishRelease(repo, releaseName, new gitFlowErrorsListener());
                    repo.update();

                    String publishedReleaseMessage = String.format("A new remote branch '%s%s' was created", releasePrefix, releaseName);

                    GitUIUtil.notifySuccess(myProject, releaseName, publishedReleaseMessage);
                }
            }.queue();

        }
    }

    private class TrackReleaseAction extends DumbAwareAction{

        TrackReleaseAction(){
            super("Track Release");
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

            ArrayList<String> remoteBranches = new ArrayList<String>(myMultiRootBranchConfig.getRemoteBranches());
            ArrayList<String> remoteReleaseBranches = new ArrayList<String>();

            //get only the branches with the proper prefix
            for(Iterator<String> i = remoteBranches.iterator(); i.hasNext(); ) {
                String item = i.next();
                if (item.contains(releasePrefix)){
                    remoteReleaseBranches.add(item);
                }
            }

            if (remoteBranches.size()>0){
                GitflowBranchChooseDialog branchChoose = new GitflowBranchChooseDialog(myProject,remoteReleaseBranches);

                branchChoose.show();
                if (branchChoose.isOK()){
                    String branchName= branchChoose.getSelectedBranchName();
                    final String releaseName= GitflowConfigUtil.getReleaseNameFromBranch(myProject, branchName);


                    new Task.Backgroundable(myProject,"Tracking release "+releaseName,false){
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            myGitflow.trackRelease(repo, releaseName, new gitFlowErrorsListener());
                            repo.update();

                            String trackedReleaseMessage = String.format(" A new remote tracking branch '%s$s' was created", releasePrefix, releaseName);

                            GitUIUtil.notifySuccess(myProject, releaseName, trackedReleaseMessage);
                        }
                    }.queue();
                }
            }
            else{
                new Notification(GitVcs.IMPORTANT_ERROR_NOTIFICATION.getDisplayId(), "Error", "No remote branches", NotificationType.ERROR).notify(myProject);
            }

        }
    }



    private class gitFlowErrorsListener extends gitflowLineHandler{

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


    //generic line handler (should handle errors etc)
    private abstract class gitflowLineHandler implements GitLineHandlerListener {

        @Override
        public void onLineAvailable(String line, Key outputType) {}

        @Override
        public void processTerminated(int exitCode) {}

        @Override
        public void startFailed(Throwable exception) {}
    }



}
