package gitflow.actions;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import gitflow.Gitflow;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowBranchUtilManager;
import gitflow.GitflowConfigUtil;
import org.jetbrains.annotations.NotNull;

/**
 * All actions associated with Gitflow
 *
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */
public class GitflowActions {
    Project myProject;
    Gitflow myGitflow = ServiceManager.getService(Gitflow.class);
    GitRepository repo;
    VirtualFileManager virtualFileMananger;

//    String featurePrefix;
//    String releasePrefix;
//    String hotfixPrefix;
//    String masterBranch;
//    String developBranch;
//

    public GitflowActions(@NotNull Project project){
        myProject=project;
        virtualFileMananger = VirtualFileManager.getInstance();
    }

    //constructs the actions for the widget popup
    public ActionGroup getActionsForRepo(GitRepository repo){
        DefaultActionGroup actionGroup = new DefaultActionGroup(null, false);

        GitflowBranchUtil branchUtil = GitflowBranchUtilManager.getBranchUtil(repo);


        boolean noRemoteTrackBranches = false;
        boolean noRemoteFeatureBranches = false;

        boolean trackedAllFeatureBranches = false;
        boolean trackedAllReleaseBranches = false;

        String currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);

        String featurePrefix = GitflowConfigUtil.getFeaturePrefix(myProject, repo);
        String releasePrefix = GitflowConfigUtil.getReleasePrefix(myProject, repo);
        String hotfixPrefix= GitflowConfigUtil.getHotfixPrefix(myProject, repo);
        String masterBranch= GitflowConfigUtil.getMasterBranch(myProject, repo);
        String developBranch= GitflowConfigUtil.getDevelopBranch(myProject, repo);

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
            actionGroup.add(new InitRepoAction(repo));
        }
        else{

            //FEATURE ACTIONS

            actionGroup.addSeparator("Feature");
            actionGroup.add(new StartFeatureAction(repo));
            //feature only actions
            if (branchUtil.isCurrentBranchFeature()){
                actionGroup.add(new FinishFeatureAction(repo));

                //can't publish feature if it's already published
                if (branchUtil.isCurrentBranchPublished()==false){
                    actionGroup.add(new PublishFeatureAction(repo));
                }
            }

            //make sure there's a feature to track, and that not all features are tracked
            if (noRemoteFeatureBranches == false && trackedAllFeatureBranches == false){
                actionGroup.add(new TrackFeatureAction(repo));
            }


            //RELEASE ACTIONS

            actionGroup.addSeparator("Release");
            actionGroup.add(new StartReleaseAction(repo));
            //release only actions
            if (branchUtil.isCurrentBranchRelease()){
                actionGroup.add(new FinishReleaseAction(repo));

                //can't publish release if it's already published
                if (branchUtil.isCurrentBranchPublished()==false){
                    actionGroup.add(new PublishReleaseAction(repo));
                }
            }

            //make sure there's something to track and that not all features are tracked
            if (noRemoteTrackBranches==false  && trackedAllReleaseBranches ==false){
                actionGroup.add(new TrackReleaseAction(repo));
            }


            //HOTFIX ACTIONS
            actionGroup.addSeparator("Hotfix");

            //master only actions
            actionGroup.add(new StartHotfixAction(repo));
            if (branchUtil.isCurrentBranchHotfix()){
                actionGroup.add(new FinishHotfixAction(repo));

                //can't publish hotfix if it's already published
                if (branchUtil.isCurrentBranchPublished() == false) {
                    actionGroup.add(new PublishHotfixAction(repo));
                }
            }

        }

        return actionGroup;
    }

    public static void runMergeTool(){
        git4idea.actions.GitResolveConflictsAction resolveAction= new git4idea.actions.GitResolveConflictsAction();
        AnActionEvent e = new AnActionEvent(null, DataManager.getInstance().getDataContext(), ActionPlaces.UNKNOWN, new Presentation(""), ActionManager.getInstance(), 0);
        resolveAction.actionPerformed(e);
    }


}
