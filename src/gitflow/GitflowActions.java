package gitflow;

import com.intellij.ide.DataManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitPlatformFacade;
import git4idea.GitUtil;
import git4idea.GitVcs;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitLineHandlerListener;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import gitflow.ui.GitflowBranchChooseDialog;
import git4idea.util.GitUIUtil;
import git4idea.validators.GitNewBranchNameValidator;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.Iterator;

/**
 * All actions associated with Gitflow
 *
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */
public class GitflowActions {
    Project myProject;
    Gitflow myGitflow = ServiceManager.getService(Gitflow.class);
    GitRepositoryManager myRepositoryManager;
    GitRepository repo;
    GitflowBranchUtil branchUtil;
    private VcsDirtyScopeManager myDirtyScopeManager;

    String currentBranchName;

    String featurePrefix;
    String releasePrefix;
    String hotfixPrefix;
    String masterBranch;
    String developBranch;

    boolean noRemoteTrackBranches;
    boolean noRemoteFeatureBranches;

    boolean trackedAllFeatureBranches;
    boolean trackedAllReleaseBranches;

    public GitflowActions(@NotNull Project project){
        myProject=project;

        branchUtil=new GitflowBranchUtil(project);

        myRepositoryManager = GitUtil.getRepositoryManager(myProject);
        repo = GitBranchUtil.getCurrentRepository(myProject);
        if (repo!=null){
            currentBranchName= GitBranchUtil.getBranchNameOrRev(repo);
        }

        featurePrefix = GitflowConfigUtil.getFeaturePrefix(myProject);
        releasePrefix = GitflowConfigUtil.getReleasePrefix(myProject);
        hotfixPrefix= GitflowConfigUtil.getHotfixPrefix(myProject);
        masterBranch= GitflowConfigUtil.getMasterBranch(myProject);
        developBranch= GitflowConfigUtil.getDevelopBranch(myProject);

        if (releasePrefix!=null){
            noRemoteTrackBranches = branchUtil.getRemoteBranchesWithPrefix(releasePrefix).isEmpty();
            trackedAllReleaseBranches = branchUtil.areAllBranchesTracked(releasePrefix);
        }
        if (featurePrefix!=null){
            noRemoteFeatureBranches = branchUtil.getRemoteBranchesWithPrefix(featurePrefix).isEmpty();
            trackedAllFeatureBranches = branchUtil.areAllBranchesTracked(featurePrefix);
        }


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

            //make sure there's a feature to track, and that not all features are track
            if (noRemoteFeatureBranches ==false && trackedAllFeatureBranches ==false){
                actionGroup.add(new TrackFeatureAction());
            }


            //RELEASE ACTIONS

            actionGroup.addSeparator("Release");
            actionGroup.add(new StartReleaseAction());
            //release only actions
            if (branchUtil.isCurrentBranchRelease()){
                actionGroup.add(new FinishReleaseAction());

                //can't publish release if it's already published
                if (branchUtil.isCurrentBranchPublished()==false){
                    actionGroup.add(new PublishReleaseAction());
                }
            }

            //make sure there's something to track and that not all features are tracked
            if (noRemoteTrackBranches==false  && trackedAllReleaseBranches ==false){
                actionGroup.add(new TrackReleaseAction());
            }


            //HOTFIX ACTIONS
            actionGroup.addSeparator("Hotfix");

            //master only actions
            actionGroup.add(new StartHotfixAction());
            if (branchUtil.isCurrentBranchHotfix()){
                actionGroup.add(new FinishHotixAction());
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
            final gitFlowErrorsListener errorLineHandler = new gitFlowErrorsListener();
            final LineHandler localLineHandler = new LineHandler();

            new Task.Backgroundable(myProject,"Initializing repo",false){
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitCommandResult result = myGitflow.initRepo(repo, errorLineHandler, localLineHandler);

                    if (result.success()){
                        String publishedFeatureMessage = String.format("Initialized gitflow repo");
                        GitUIUtil.notifySuccess(myProject, publishedFeatureMessage,"");
                    }
                    else{
                        GitUIUtil.notifyError(myProject,"Error",result.getErrorOutputAsHtmlString()+" "+errorLineHandler.getErrors()+" "+localLineHandler.getErrors());
                    }

                    repo.update();
                }
            }.queue();


        }

        private class LineHandler extends gitflowLineHandler{
            @Override
            public void onLineAvailable(String line, Key outputType) {
                if (line.contains("Already initialized for gitflow")){
                   myErrors.add("Repo already initialized for gitflow");
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
            final gitFlowErrorsListener errorLineHandler = new gitFlowErrorsListener();

            if (featureName!=null){
                    new Task.Backgroundable(myProject,"Starting feature "+featureName,false){
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            GitCommandResult result =  myGitflow.startFeature(repo,featureName,new gitFlowErrorsListener());


                            if (result.success()){
                                String startedFeatureMessage = String.format("A new branch '%s%s' was created, based on '%s'", featurePrefix, featureName, developBranch);
                                GitUIUtil.notifySuccess(myProject, featureName, startedFeatureMessage );
                            }
                            else{
                                GitUIUtil.notifyError(myProject,"Error",result.getErrorOutputAsHtmlString()+" "+errorLineHandler.getErrors());
                            }

                            repo.update();

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
                final gitFlowErrorsListener errorLineHandler = new gitFlowErrorsListener();

                new Task.Backgroundable(myProject,"Finishing feature "+featureName,false){
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        GitCommandResult result =  myGitflow.finishFeature(repo,featureName,errorLineHandler);


                        if (result.success()){
                            String finishedFeatureMessage = String.format("The feature branch '%s%s' was merged into '%s'", featurePrefix, featureName, developBranch);
                            GitUIUtil.notifySuccess(myProject, featureName, finishedFeatureMessage);
                        }
                        else{

                            GitUIUtil.notifyError(myProject,"Error",result.getErrorOutputAsHtmlString()+" "+errorLineHandler.getErrors());

                        }

                        repo.update();

                    }

                    @Override
                    public void onSuccess() {
                        super.onSuccess();

                        if (errorLineHandler.hasMergeError){
                            runMergeTool();
                        }
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
            final gitFlowErrorsListener errorLineHandler = new gitFlowErrorsListener();

            new Task.Backgroundable(myProject,"Publishing feature "+featureName,false){
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitCommandResult result = myGitflow.publishFeature(repo,featureName,new gitFlowErrorsListener());

                    if (result.success()){
                        String publishedFeatureMessage = String.format("A new remote branch '%s%s' was created", featurePrefix, featureName);
                        GitUIUtil.notifySuccess(myProject, featureName, publishedFeatureMessage);
                    }
                    else{
                        GitUIUtil.notifyError(myProject,"Error",result.getErrorOutputAsHtmlString()+" "+errorLineHandler.getErrors());
                    }

                    repo.update();


                }
            }.queue();

        }
    }

    private class TrackFeatureAction extends DumbAwareAction{

        TrackFeatureAction(){
            super("Track Feature");
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

            ArrayList<String> remoteBranches = branchUtil.getRemoteBranchNames();
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
                    final GitRemote remote=branchUtil.getRemoteByBranch(branchName);
                    final gitFlowErrorsListener errorLineHandler = new gitFlowErrorsListener();

                    new Task.Backgroundable(myProject,"Tracking feature "+featureName,false){
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            GitCommandResult result = myGitflow.trackFeature(repo, featureName, remote, errorLineHandler);

                            if (result.success()){
                                String trackedFeatureMessage = String.format("A new branch '%s%s' was created", featurePrefix, featureName);
                                GitUIUtil.notifySuccess(myProject, featureName, trackedFeatureMessage);

                            }
                            else{
                                GitUIUtil.notifyError(myProject,"Error",result.getErrorOutputAsHtmlString()+" "+errorLineHandler.getErrors());
                            }

                            repo.update();

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
            final gitFlowErrorsListener errorLineHandler = new gitFlowErrorsListener();

            if (releaseName!=null){
                new Task.Backgroundable(myProject,"Starting release "+releaseName,false){
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        GitCommandResult result=  myGitflow.startRelease(repo, releaseName, errorLineHandler);

                        if (result.success()){
                            String startedReleaseMessage = String.format("A new release '%s%s' was created, based on '%s'", releasePrefix, releaseName, developBranch);
                            GitUIUtil.notifySuccess(myProject, releaseName, startedReleaseMessage );
                        }
                        else{
                            GitUIUtil.notifyError(myProject,"Error",result.getErrorOutputAsHtmlString()+" "+errorLineHandler.getErrors());
                        }

                        repo.update();

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

                final String releaseName = GitflowConfigUtil.getReleaseNameFromBranch(myProject, currentBranchName);
                final gitFlowErrorsListener errorLineHandler = new gitFlowErrorsListener();
                String defaultTagMessage="Tagging version "+releaseName;

                final String tagMessage = Messages.showInputDialog(myProject, "Enter the tag message:", "Finish Release", Messages.getQuestionIcon(), defaultTagMessage, null);

                if (tagMessage!=null){
                    new Task.Backgroundable(myProject,"Finishing release "+releaseName,false){
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            GitCommandResult result =  myGitflow.finishRelease(repo, releaseName, tagMessage, errorLineHandler);

                            if (result.success()){
                                String finishedReleaseMessage = String.format("The release branch '%s%s' was merged into '%s' and '%s'", featurePrefix, releaseName, developBranch, masterBranch);
                                GitUIUtil.notifySuccess(myProject, releaseName, finishedReleaseMessage);
                            }
                            else{
                                GitUIUtil.notifyError(myProject,"Error",result.getErrorOutputAsHtmlString()+" "+errorLineHandler.getErrors());
                            }

                            repo.update();

                        }
                    }.queue();

                }
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
            final gitFlowErrorsListener errorLineHandler = new gitFlowErrorsListener();

            new Task.Backgroundable(myProject,"Publishing release "+releaseName,false){
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitCommandResult result = myGitflow.publishRelease(repo, releaseName, errorLineHandler);

                    if (result.success()){
                        String publishedReleaseMessage = String.format("A new remote branch '%s%s' was created", releasePrefix, releaseName);
                        GitUIUtil.notifySuccess(myProject, releaseName, publishedReleaseMessage);
                    }
                    else{
                        GitUIUtil.notifyError(myProject,"Error",result.getErrorOutputAsHtmlString()+" "+errorLineHandler.getErrors());
                    }

                    repo.update();
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

            ArrayList<String> remoteBranches = branchUtil.getRemoteBranchNames();
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
                    final gitFlowErrorsListener errorLineHandler = new gitFlowErrorsListener();

                    new Task.Backgroundable(myProject,"Tracking release "+releaseName,false){
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            GitCommandResult result = myGitflow.trackRelease(repo, releaseName, errorLineHandler);

                            if (result.success()){
                                String trackedReleaseMessage = String.format(" A new remote tracking branch '%s$s' was created", releasePrefix, releaseName);
                                GitUIUtil.notifySuccess(myProject, releaseName, trackedReleaseMessage);
                            }
                            else{
                                GitUIUtil.notifyError(myProject,"Error",result.getErrorOutputAsHtmlString()+" "+errorLineHandler.getErrors());
                            }

                            repo.update();
                        }
                    }.queue();
                }
            }
            else{
                new Notification(GitVcs.IMPORTANT_ERROR_NOTIFICATION.getDisplayId(), "Error", "No remote branches", NotificationType.ERROR).notify(myProject);
            }

        }
    }


    //hotfix actions

    private class StartHotfixAction extends DumbAwareAction {
        private ArrayList<GitRepository> repos = new ArrayList<GitRepository>();

        StartHotfixAction() {
            super("Start Hotfix");
            repos.add(repo);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

            final String hotfixName = Messages.showInputDialog(myProject, "Enter the name of the new hotfix:", "New Hotfix", Messages.getQuestionIcon(), "",
                    GitNewBranchNameValidator.newInstance(repos));
            final gitFlowErrorsListener errorLineHandler = new gitFlowErrorsListener();

            if (hotfixName!=null){
                new Task.Backgroundable(myProject,"Starting hotfix "+hotfixName,false){
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        GitCommandResult result =  myGitflow.startHotfix(repo, hotfixName, errorLineHandler);

                        if (result.success()){
                            String startedHotfixMessage = String.format("A new hotfix '%s%s' was created, based on '%s'", hotfixPrefix, hotfixName, developBranch);
                            GitUIUtil.notifySuccess(myProject, hotfixName, startedHotfixMessage );
                        }
                        else{
                            GitUIUtil.notifyError(myProject,"Error",result.getErrorOutputAsHtmlString()+" "+errorLineHandler.getErrors());
                        }

                        repo.update();

                    }
                }.queue();

            }

        }
    }

    private class FinishHotixAction extends DumbAwareAction{

        FinishHotixAction() {
            super("Finish Hotfix");
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

            String currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);


            if (currentBranchName.isEmpty()==false){

                //TODO HOTFIX NAME
                final String hotfixName = GitflowConfigUtil.getHotfixNameFromBranch(myProject, currentBranchName);

                String defaultTagMessage="Tagging version "+hotfixName;

                final String tagMessage = Messages.showInputDialog(myProject, "Enter the tag message:", "Finish Hotfix", Messages.getQuestionIcon(), defaultTagMessage, null);
                final gitFlowErrorsListener errorLineHandler = new gitFlowErrorsListener();

                if (tagMessage!=null){
                    new Task.Backgroundable(myProject,"Finishing hotfix "+hotfixName,false){
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            GitCommandResult result=  myGitflow.finishHotfix(repo, hotfixName, tagMessage, errorLineHandler);

                            if (result.success()){
                                String finishedHotfixMessage = String.format("The hotfix branch '%s%s' was merged into '%s' and '%s'", hotfixPrefix, hotfixName, developBranch, masterBranch);
                                GitUIUtil.notifySuccess(myProject, hotfixName, finishedHotfixMessage);
                            }
                            else{
                                GitUIUtil.notifyError(myProject,"Error",result.getErrorOutputAsHtmlString()+" "+errorLineHandler.getErrors());
                            }

                            repo.update();

                        }
                    }.queue();
                }
            }

        }

    }

    public void runMergeTool(){

         GitRepository rep = myRepositoryManager.getRepositories().get(0);
        VirtualFile root = rep.getRoot();
        ServiceManager.getService(myProject, GitPlatformFacade.class).hardRefresh(root);
        rep.update();

        git4idea.actions.GitResolveConflictsAction resolveAction= new git4idea.actions.GitResolveConflictsAction();
        AnActionEvent e = new AnActionEvent(null, DataManager.getInstance().getDataContext(), ActionPlaces.UNKNOWN, new Presentation(""), ActionManager.getInstance(), 0);
        resolveAction.actionPerformed(e);
    }


    private class gitFlowErrorsListener extends gitflowLineHandler{

        boolean hasMergeError=false;

        @Override
        public void onLineAvailable(String line, Key outputType) {
            if (line.contains("'flow' is not a git command")){
                GitUIUtil.notifyError(myProject,"Error","Gitflow is not installed");
            }
            if (line.contains("Not a gitflow-enabled repo yet")){
                GitUIUtil.notifyError(myProject,"Error","Not a gitflow-enabled repo yet. Please init git flow");
            }
            if (line.contains("There were merge conflicts")){
                hasMergeError=true;
            }
        }

    };


    //generic line handler (should handle errors etc)
    private abstract class gitflowLineHandler implements GitLineHandlerListener {
        ArrayList<String> myErrors=new ArrayList<String>();

        @Override
        public void onLineAvailable(String line, Key outputType) {
                if (line.contains("fatal") || line.contains("Fatal")){
                    myErrors.add(line);
                }
            }

        @Override
        public void processTerminated(int exitCode) {}

        @Override
        public void startFailed(Throwable exception) {}

        public String getErrors(){
            return StringUtils.join(myErrors,",");
        }
    }



}
