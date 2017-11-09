package gitflow.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowBranchUtilManager;
import gitflow.GitflowConfigUtil;
import gitflow.BranchActionGroup;
import gitflow.PopupElementWithAdditionalInfo;
import gitflow.actions.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

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


    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent e) {
        ArrayList<AnAction> children = new ArrayList<AnAction>();

        GitflowBranchUtil branchUtil = GitflowBranchUtilManager.getBranchUtil(myRepo);


        boolean noRemoteTrackBranches = false;
        boolean noRemoteFeatureBranches = false;

        boolean trackedAllFeatureBranches = false;
        boolean trackedAllReleaseBranches = false;

        String currentBranchName = GitBranchUtil.getBranchNameOrRev(myRepo);

        String featurePrefix = GitflowConfigUtil.getFeaturePrefix(myProject, myRepo);
        String releasePrefix = GitflowConfigUtil.getReleasePrefix(myProject, myRepo);
        String hotfixPrefix= GitflowConfigUtil.getHotfixPrefix(myProject, myRepo);
        String masterBranch= GitflowConfigUtil.getMasterBranch(myProject, myRepo);
        String developBranch= GitflowConfigUtil.getDevelopBranch(myProject, myRepo);

        if (releasePrefix!=null){
            noRemoteTrackBranches = branchUtil.getRemoteBranchesWithPrefix(releasePrefix).isEmpty();
            trackedAllReleaseBranches = branchUtil.areAllBranchesTracked(releasePrefix);
        }
        if (featurePrefix!=null){
            noRemoteFeatureBranches = branchUtil.getRemoteBranchesWithPrefix(featurePrefix).isEmpty();
            trackedAllFeatureBranches = branchUtil.areAllBranchesTracked(featurePrefix);
        }

        //gitflow not setup
        if (branchUtil.hasGitflow()!=true){
            children.add(new InitRepoAction(myRepo));
        }
        else{

            //FEATURE ACTIONS

            children.add(new Separator("Feature"));
            children.add(new StartFeatureAction(myRepo));
            //feature only actions
            if (branchUtil.isCurrentBranchFeature()){
                children.add(new FinishFeatureAction(myRepo));

                //can't publish feature if it's already published
                if (branchUtil.isCurrentBranchPublished()==false){
                    children.add(new PublishFeatureAction(myRepo));
                }
            }

            //make sure there's a feature to track, and that not all features are tracked
            if (noRemoteFeatureBranches == false && trackedAllFeatureBranches == false){
                children.add(new TrackFeatureAction(myRepo));
            }


            //RELEASE ACTIONS

            children.add(new Separator("Release"));
            children.add(new StartReleaseAction(myRepo));
            //release only actions
            if (branchUtil.isCurrentBranchRelease()){
                children.add(new FinishReleaseAction(myRepo));

                //can't publish release if it's already published
                if (branchUtil.isCurrentBranchPublished()==false){
                    children.add(new PublishReleaseAction(myRepo));
                }
            }

            //make sure there's something to track and that not all features are tracked
            if (noRemoteTrackBranches==false  && trackedAllReleaseBranches ==false){
                children.add(new TrackReleaseAction(myRepo));
            }


            //HOTFIX ACTIONS
            children.add(new Separator("Hotfix"));

            //master only actions
            children.add(new StartHotfixAction(myRepo));
            if (branchUtil.isCurrentBranchHotfix()){
                children.add(new FinishHotfixAction(myRepo));

                //can't publish hotfix if it's already published
                if (branchUtil.isCurrentBranchPublished() == false) {
                    children.add(new PublishHotfixAction(myRepo));
                }
            }

        }

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

