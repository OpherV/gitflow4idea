package gitflowavh.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFileManager;
import git4idea.branch.GitBranchUtil;
import git4idea.merge.GitMerger;
import git4idea.repo.GitRepository;
import gitflowavh.GitFlowAVH;
import gitflowavh.GitFlowAVHBranchUtil;
import gitflowavh.GitFlowAVHConfigUtil;
import gitflowavh.ui.NotifyUtil;

import java.util.ArrayList;

public abstract class GitFlowAVHAction extends DumbAwareAction {
    Project myProject;
    GitFlowAVH myGitflow = ServiceManager.getService(GitFlowAVH.class);
    ArrayList<GitRepository> repos = new ArrayList<GitRepository>();
    GitRepository repo;
    GitFlowAVHBranchUtil branchUtil;

    VirtualFileManager virtualFileMananger;


    String currentBranchName;

    String featurePrefix;
    String releasePrefix;
    String hotfixPrefix;
    String masterBranch;
    String developBranch;

    GitFlowAVHAction(String actionName) {
        super(actionName);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        virtualFileMananger = VirtualFileManager.getInstance();
        myProject = e.getProject();
        branchUtil = new GitFlowAVHBranchUtil(myProject);
        repo = GitBranchUtil.getCurrentRepository(myProject);
        repos.add(repo);

        if (repo != null) {
            currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);
        }

        featurePrefix = GitFlowAVHConfigUtil.getFeaturePrefix(myProject);
        releasePrefix = GitFlowAVHConfigUtil.getReleasePrefix(myProject);
        hotfixPrefix = GitFlowAVHConfigUtil.getHotfixPrefix(myProject);
        masterBranch = GitFlowAVHConfigUtil.getMasterBranch(myProject);
        developBranch = GitFlowAVHConfigUtil.getDevelopBranch(myProject);
    }

    //returns true if merge successful, false otherwise
    public boolean handleMerge() {
        //ugly, but required for intellij to catch up with the external changes made by
        //the CLI before being able to run the merge tool
        virtualFileMananger.syncRefresh();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }


        GitFlowAVHActions.runMergeTool();
        repo.update();

        //if merge was completed successfully, finish the action
        //note that if it wasn't intellij is left in the "merging state", and git4idea provides no UI way to resolve it
        //merging can be done via intellij itself or any other util
        int answer = Messages.showYesNoDialog(myProject, "Was the merge completed succesfully?", "Merge", Messages.getQuestionIcon());
        if (answer == 0) {
            GitMerger gitMerger = new GitMerger(myProject);

            try {
                gitMerger.mergeCommit(gitMerger.getMergingRoots());
            } catch (VcsException e1) {
                NotifyUtil.notifyError(myProject, "Error", "Error committing merge result");
                e1.printStackTrace();
            }

            return true;
        } else {

            NotifyUtil.notifyInfo(myProject, "Merge incomplete", "To manually complete the merge choose VCS > Git > Resolve Conflicts.\n" +
                    "Once done, commit the merged files.\n");
            return false;
        }


    }
}
