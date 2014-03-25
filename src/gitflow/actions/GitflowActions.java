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
            actionGroup.add(new InitRepoAction());
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

    public static void runMergeTool(){
        git4idea.actions.GitResolveConflictsAction resolveAction= new git4idea.actions.GitResolveConflictsAction();
        AnActionEvent e = new AnActionEvent(null, DataManager.getInstance().getDataContext(), ActionPlaces.UNKNOWN, new Presentation(""), ActionManager.getInstance(), 0);
        resolveAction.actionPerformed(e);
    }


}
