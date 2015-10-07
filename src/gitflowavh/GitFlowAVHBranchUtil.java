package gitflowavh;

import com.intellij.openapi.project.Project;
import git4idea.GitLocalBranch;
import git4idea.GitRemoteBranch;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;

import java.util.ArrayList;
import java.util.Collection;


public class GitFlowAVHBranchUtil {
    Project myProject;
    GitRepository repo;

    String currentBranchName;
    String branchnameMaster;
    String prefixFeature;
    String prefixRelease;
    String prefixHotfix;
    String prefixBugfix;


    public GitFlowAVHBranchUtil(Project project) {
        myProject = project;
        repo = GitBranchUtil.getCurrentRepository(project);

        assert repo != null;
        currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);

        branchnameMaster = GitFlowAVHConfigUtil.getMasterBranch(project);
        prefixFeature = GitFlowAVHConfigUtil.getFeaturePrefix(project);
        prefixRelease = GitFlowAVHConfigUtil.getReleasePrefix(project);
        prefixHotfix = GitFlowAVHConfigUtil.getHotfixPrefix(project);
        prefixBugfix = GitFlowAVHConfigUtil.getBugfixPrefix(project);
    }

    public boolean hasGitflow() {
        boolean hasGitflow;
        hasGitflow = GitFlowAVHConfigUtil.getMasterBranch(myProject) != null
                && GitFlowAVHConfigUtil.getDevelopBranch(myProject) != null
                && GitFlowAVHConfigUtil.getFeaturePrefix(myProject) != null
                && GitFlowAVHConfigUtil.getReleasePrefix(myProject) != null
                && GitFlowAVHConfigUtil.getHotfixPrefix(myProject) != null
                && GitFlowAVHConfigUtil.getBugfixPrefix(myProject) != null;

        return hasGitflow;
    }

    /*public boolean isCurrentBranchMaster(){
        return currentBranchName.startsWith(branchnameMaster);
    }*/

    /**
     * Checks whether the current branch is a feature branch.
     */
    public boolean isCurrentBranchFeature() {
        return currentBranchName.startsWith(prefixFeature);
    }

    /**
     * Checks whether the current branch is a release branch.
     */
    public boolean isCurrentBranchRelease() {
        return currentBranchName.startsWith(prefixRelease);
    }

    /**
     * Checks whether the current branch is a hotfix branch.
     */
    public boolean isCurrentBranchHotfix() {
        return currentBranchName.startsWith(prefixHotfix);
    }

    /**
     * Checks whether the current branch is a bugfix branch.
     */
    public boolean isCurrentBranchBugfix() {
        return currentBranchName.startsWith(prefixBugfix);
    }

    /**
     * Checks whether the current branch also exists on the remote.
     */
    public boolean isCurrentBranchPublished() {
        return !getRemoteBranchesWithPrefix(currentBranchName).isEmpty();
    }

    /**
     * If no prefix specified, returns all remote branches
     */
    public ArrayList<String> getRemoteBranchesWithPrefix(String prefix) {
        ArrayList<String> remoteBranches = getRemoteBranchNames();
        ArrayList<String> selectedBranches = new ArrayList<String>();

        for (String branch : remoteBranches) {
            if (branch.contains(prefix)) {
                selectedBranches.add(branch);
            }
        }

        return selectedBranches;
    }

    public ArrayList<String> filterBranchListByPrefix(Collection<String> inputBranches, String prefix) {
        ArrayList<String> outputBranches = new ArrayList<String>();

        for (String branch : inputBranches) {
            if (branch.contains(prefix)) {
                outputBranches.add(branch);
            }
        }

        return outputBranches;
    }

    public ArrayList<String> getRemoteBranchNames() {
        ArrayList<GitRemoteBranch> remoteBranches = new ArrayList<GitRemoteBranch>(repo.getBranches().getRemoteBranches());
        ArrayList<String> branchNameList = new ArrayList<String>();

        for (GitRemoteBranch branch : remoteBranches) {
            branchNameList.add(branch.getName());
        }

        return branchNameList;
    }

    public ArrayList<String> getLocalBranchNames() {
        ArrayList<GitLocalBranch> localBranches = new ArrayList<GitLocalBranch>(repo.getBranches().getLocalBranches());
        ArrayList<String> branchNameList = new ArrayList<String>();

        for (GitLocalBranch branch : localBranches) {
            branchNameList.add(branch.getName());
        }

        return branchNameList;
    }

    public GitRemote getRemoteByBranch(String branchName) {
        GitRemote remote = null;

        ArrayList<GitRemoteBranch> remoteBranches = new ArrayList<GitRemoteBranch>(repo.getBranches().getRemoteBranches());

        for (GitRemoteBranch branch : remoteBranches) {
            if (branch.getName().equals(branchName)) {
                remote = branch.getRemote();
                break;
            }
        }

        return remote;
    }

    public boolean areAllBranchesTracked(String prefix) {
        ArrayList<String> localBranches = filterBranchListByPrefix(getLocalBranchNames(), prefix);

        // To avoid a vacuous truth value. That is, when no branches at all exist,
        // they shouldn't be considered as "all pulled"
        if (localBranches.isEmpty()) {
            return false;
        }

        ArrayList<String> remoteBranches = getRemoteBranchNames();

        // Check that every local branch has a matching remote branch
        for (String localBranch : localBranches) {
            boolean hasMatchingRemoteBranch = false;

            for (String remoteBranch : remoteBranches) {
                if (remoteBranch.contains(localBranch)) {
                    hasMatchingRemoteBranch = true;
                    break;
                }
            }

            // At least one matching branch wasn't found
            if (!hasMatchingRemoteBranch) {
                return false;
            }
        }

        return true;
    }
}
