package gitflow.actions;

import com.intellij.dvcs.ui.BranchActionGroup;
import com.intellij.dvcs.ui.PopupElementWithAdditionalInfo;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowBranchUtilManager;
import gitflow.GitflowConfigUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;

class RepoActions extends BranchActionGroup implements PopupElementWithAdditionalInfo, FileEditorManagerListener {
    Project myProject;
    GitRepository myRepo;

    RepoActions(@NotNull Project project, @NotNull GitRepository repo) {
        myProject = project;
        myRepo = repo;

        String repoName = repo.getRoot().getPresentableName();
        getTemplatePresentation().setText(repoName, false); // no mnemonics
        this.updateFavoriteIcon();
        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);
    }

    public ArrayList<AnAction> getRepoActions(){
        ArrayList<AnAction> actionList = new ArrayList<AnAction>();

        GitflowBranchUtil branchUtil = GitflowBranchUtilManager.getBranchUtil(myRepo);


        boolean noRemoteTrackBranches = false;
        boolean noRemoteFeatureBranches = false;
        boolean noRemoteBugfixBranches = false;

        boolean trackedAllFeatureBranches = false;
        boolean trackedAllReleaseBranches = false;
        boolean trackedAllBugfixBranches = false;

        String currentBranchName = GitBranchUtil.getBranchNameOrRev(myRepo);

        String featurePrefix = GitflowConfigUtil.getFeaturePrefix(myProject, myRepo);
        String releasePrefix = GitflowConfigUtil.getReleasePrefix(myProject, myRepo);
        String hotfixPrefix= GitflowConfigUtil.getHotfixPrefix(myProject, myRepo);
        String masterBranch= GitflowConfigUtil.getMasterBranch(myProject, myRepo);
        String developBranch= GitflowConfigUtil.getDevelopBranch(myProject, myRepo);
        String bugfixPrefix = GitflowConfigUtil.getBugfixPrefix(myProject, myRepo);

        if (releasePrefix!=null){
            noRemoteTrackBranches = branchUtil.getRemoteBranchesWithPrefix(releasePrefix).isEmpty();
            trackedAllReleaseBranches = branchUtil.areAllBranchesTracked(releasePrefix);
        }
        if (featurePrefix!=null){
            noRemoteFeatureBranches = branchUtil.getRemoteBranchesWithPrefix(featurePrefix).isEmpty();
            trackedAllFeatureBranches = branchUtil.areAllBranchesTracked(featurePrefix);
        }
        if (bugfixPrefix!=null){
            noRemoteBugfixBranches = branchUtil.getRemoteBranchesWithPrefix(bugfixPrefix).isEmpty();
            trackedAllBugfixBranches = branchUtil.areAllBranchesTracked(bugfixPrefix);
        }

        //gitflow not setup
        if (!branchUtil.hasGitflow()){
            actionList.add(new InitRepoAction(myRepo));
        } else {

            //FEATURE ACTIONS

            actionList.add(new Separator("Feature"));
            actionList.add(new StartFeatureAction(myRepo));
            //feature only actions
            if (branchUtil.isCurrentBranchFeature()){
                actionList.add(new FinishFeatureAction(myRepo));

                //can't publish feature if it's already published
                if (!branchUtil.isCurrentBranchPublished()){
                    actionList.add(new PublishFeatureAction(myRepo));
                }
            }

            //make sure there's a feature to track, and that not all features are tracked
            if (!noRemoteFeatureBranches && !trackedAllFeatureBranches){
                actionList.add(new TrackFeatureAction(myRepo));
            }


            //RELEASE ACTIONS

            actionList.add(new Separator("Release"));
            actionList.add(new StartReleaseAction(myRepo));
            //release only actions
            if (branchUtil.isCurrentBranchRelease()){
                actionList.add(new FinishReleaseAction(myRepo));

                //can't publish release if it's already published
                if (!branchUtil.isCurrentBranchPublished()){
                    actionList.add(new PublishReleaseAction(myRepo));
                }
            }

            //make sure there's something to track and that not all features are tracked
            if (!noRemoteTrackBranches && !trackedAllReleaseBranches){
                actionList.add(new TrackReleaseAction(myRepo));
            }


            //BUGFIX ACTIONS

            actionList.add(new Separator("Bugfix"));
            actionList.add(new StartBugfixAction(myRepo));
            //bugfix only actions
            if (branchUtil.isCurrentBranchBugfix()){
                actionList.add(new FinishBugfixAction(myRepo));

                //can't publish bugfix if it's already published
                if (branchUtil.isCurrentBranchPublished()==false){
                    actionList.add(new PublishBugfixAction(myRepo));
                }
            }

            //make sure there's a bugfix to track, and that not all bugfixes are tracked
            if (noRemoteBugfixBranches == false && trackedAllBugfixBranches == false){
                actionList.add(new TrackBugfixAction(myRepo));
            }

            //HOTFIX ACTIONS
            actionList.add(new Separator("Hotfix"));

            //master only actions
            actionList.add(new StartHotfixAction(myRepo));
            if (branchUtil.isCurrentBranchHotfix()){
                actionList.add(new FinishHotfixAction(myRepo));

                //can't publish hotfix if it's already published
                if (!branchUtil.isCurrentBranchPublished()) {
                    actionList.add(new PublishHotfixAction(myRepo));
                }
            }

            //RE-INIT ACTIONS
            actionList.add(new Separator("Repo options"));

            actionList.add(new ReInitRepoAction(myRepo));

        }

        return actionList;
    }

    public DefaultActionGroup getRepoActionGroup(){
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        Iterator actionsIterator = this.getRepoActions().iterator();
        while(actionsIterator.hasNext()){
            AnAction action = (AnAction) actionsIterator.next();
            actionGroup.add(action);
        }

        return actionGroup;
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent e) {
        ArrayList<AnAction> children = this.getRepoActions();
        return children.toArray(new AnAction[children.size()]);
    }

    @Override
    @Nullable
    public String getInfoText() {
        return "what's this";
    }

    public void updateFavoriteIcon(){
        boolean isFavorite = GitBranchUtil.getCurrentRepository(myProject) == myRepo;
        setFavorite(isFavorite);
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        this.updateFavoriteIcon();
    }
}

