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
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFileManager;
import git4idea.GitVcs;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitLineHandlerListener;
import git4idea.merge.GitMerger;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import gitflow.ui.GitflowBranchChooseDialog;
import git4idea.util.GitUIUtil;
import git4idea.validators.GitNewBranchNameValidator;
import gitflow.ui.GitflowInitOptionsDialog;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


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
    GitRepository repo;
    GitflowBranchUtil branchUtil;

    VirtualFileManager virtualFileMananger;


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
        virtualFileMananger = VirtualFileManager.getInstance();

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
                actionGroup.add(new FinishHotfixAction());

                //can't publish hotfix if it's already published
                if (branchUtil.isCurrentBranchPublished() == false) {
                    actionGroup.add(new PublishHotfixAction());
                }
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

            GitflowInitOptionsDialog optionsDialog = new GitflowInitOptionsDialog(myProject, branchUtil.getLocalBranchNames());
            optionsDialog.show();

            if(optionsDialog.isOK()) {
                final gitFlowErrorsListener errorLineHandler = new gitFlowErrorsListener();
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
                                GitUIUtil.notifyError(myProject,"Error","Please have a look at the Version Control console for more details");
                            }

                            repo.update();

                        }
                    }.queue();

            }

        }
    }

    private class FinishFeatureAction extends DumbAwareAction{

        String customFeatureName=null;

        FinishFeatureAction() {
            super("Finish Feature");
        }

        FinishFeatureAction(String name) {
            super("Finish Feature");
            customFeatureName=name;
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

            String currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);
            if (currentBranchName.isEmpty()==false){

                final AnActionEvent event=e;
                final String featureName;
                // Check if a feature name was specified, otherwise take name from current branch
                if (customFeatureName!=null){
                    featureName = customFeatureName;
                }
                else{
                    featureName = GitflowConfigUtil.getFeatureNameFromBranch(myProject, currentBranchName);
                }
                final gitFlowErrorsListener errorLineHandler = new gitFlowErrorsListener();

                new Task.Backgroundable(myProject,"Finishing feature "+featureName,false){
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        GitCommandResult result =  myGitflow.finishFeature(repo,featureName,errorLineHandler);


                        if (result.success()){
                            String finishedFeatureMessage = String.format("The feature branch '%s%s' was merged into '%s'", featurePrefix, featureName, developBranch);
                            GitUIUtil.notifySuccess(myProject, featureName, finishedFeatureMessage);
                        }
                        else if(errorLineHandler.hasMergeError){

                        }
                        else {

                            GitUIUtil.notifyError(myProject,"Error","Please have a look at the Version Control console for more details");

                        }

                    }

                    @Override
                    public void onSuccess() {
                        super.onSuccess();


                        //ugly, but required for intellij to catch up with the external changes made by
                        //the CLI before being able to run the merge tool
                        virtualFileMananger.syncRefresh();
                        try {
                            Thread.sleep(500);
                        }
                        catch (InterruptedException ignored) {
                        }


                        //TODO: refactor this logic to work in case of finishRelease as well
                        if (errorLineHandler.hasMergeError){
                            runMergeTool();
                            repo.update();

                            //if merge was completed successfully, finish the action
                            //note that if it wasn't intellij is left in the "merging state", and git4idea provides no UI way to resolve it
                            int answer = Messages.showYesNoDialog(myProject, "Was the merge completed succesfully?",  "Merge", Messages.getQuestionIcon());
                            if (answer==0){
                                GitMerger gitMerger=new GitMerger(myProject);

                                try {
                                    gitMerger.mergeCommit(gitMerger.getMergingRoots());
                                } catch (VcsException e1) {
                                    GitUIUtil.notifyError(myProject,"Error","Error committing merge result");
                                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                }

                                FinishFeatureAction completeFinishFeatureAction = new FinishFeatureAction(featureName);
                                completeFinishFeatureAction.actionPerformed(event);

                            }


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
                        GitUIUtil.notifyError(myProject,"Error","Please have a look at the Version Control console for more details");
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
                                GitUIUtil.notifyError(myProject,"Error","Please have a look at the Version Control console for more details");
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
                            GitUIUtil.notifyError(myProject,"Error","Please have a look at the Version Control console for more details");
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
                String defaultTagMessage=GitflowConfigurable.getCustomTagCommitMessage(myProject);
                defaultTagMessage=defaultTagMessage.replace("%name%", releaseName);

                String tagMessageDraft;
                final String tagMessage;

                boolean cancelAction=false;

                if (GitflowConfigurable.dontTagRelease(myProject)) {
                    tagMessage="";
                }
                else{
                    tagMessageDraft=Messages.showInputDialog(myProject, "Enter the tag message:", "Finish Release", Messages.getQuestionIcon(), defaultTagMessage, null);
                    if (tagMessageDraft==null){
                        cancelAction=true;
                        tagMessage="";
                    }
                    else{

                        tagMessage=tagMessageDraft;
                    }
                }


                if (!cancelAction){

                    new Task.Backgroundable(myProject,"Finishing release "+releaseName,false){
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            GitCommandResult result =  myGitflow.finishRelease(repo, releaseName, tagMessage, errorLineHandler);

                            if (result.success()){
                                String finishedReleaseMessage = String.format("The release branch '%s%s' was merged into '%s' and '%s'", featurePrefix, releaseName, developBranch, masterBranch);
                                GitUIUtil.notifySuccess(myProject, releaseName, finishedReleaseMessage);
                            }
                            else{
                                GitUIUtil.notifyError(myProject,"Error","Please have a look at the Version Control console for more details");
                            }



                        }

                        @Override
                        public void onSuccess() {
                            super.onSuccess();

                            virtualFileMananger.syncRefresh();
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
                        GitUIUtil.notifyError(myProject,"Error","Please have a look at the Version Control console for more details");
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
                                GitUIUtil.notifyError(myProject,"Error","Please have a look at the Version Control console for more details");
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
                            String startedHotfixMessage = String.format("A new hotfix '%s%s' was created, based on '%s'", hotfixPrefix, hotfixName, masterBranch);
                            GitUIUtil.notifySuccess(myProject, hotfixName, startedHotfixMessage );
                        }
                        else{
                            GitUIUtil.notifyError(myProject,"Error","Please have a look at the Version Control console for more details");
                        }

                        repo.update();

                    }
                }.queue();

            }

        }
    }

    private class FinishHotfixAction extends DumbAwareAction{

        FinishHotfixAction() {
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
                                GitUIUtil.notifyError(myProject,"Error","Please have a look at the Version Control console for more details");
                            }

                            repo.update();

                        }
                    }.queue();
                }
            }

        }

    }

    private class PublishHotfixAction extends DumbAwareAction {
        PublishHotfixAction() {
            super("Publish Hotfix");
        }

        @Override
        public void actionPerformed(AnActionEvent anActionEvent) {
            final String hotfixName = GitflowConfigUtil.getHotfixNameFromBranch(myProject, currentBranchName);
            final gitFlowErrorsListener errorLineHandler = new gitFlowErrorsListener();

            new Task.Backgroundable(myProject, "Publishing hotfix " + hotfixName, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitCommandResult result = myGitflow.publishHotfix(repo, hotfixName, errorLineHandler);

                    if (result.success()) {
                        String publishedHotfixMessage = String.format("A new remote branch '%s%s' was created", hotfixPrefix, hotfixName);
                        GitUIUtil.notifySuccess(myProject, hotfixName, publishedHotfixMessage);
                    } else {
                        GitUIUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                    }

                    repo.update();
                }
            }.queue();

        }
    }

    public void runMergeTool(){
        git4idea.actions.GitResolveConflictsAction resolveAction= new git4idea.actions.GitResolveConflictsAction();
        AnActionEvent e = new AnActionEvent(null, DataManager.getInstance().getDataContext(), ActionPlaces.UNKNOWN, new Presentation(""), ActionManager.getInstance(), 0);
        resolveAction.actionPerformed(e);
    }

    private abstract class gitflowTask extends Task.Backgroundable {
            public gitflowTask(@Nullable Project project, @NotNull String title) {
                super(project, title);
            }

        @Override
        public void onSuccess() {
            super.onSuccess();

        }
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
