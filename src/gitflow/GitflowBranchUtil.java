package gitflow;

import com.intellij.openapi.project.Project;
import git4idea.GitLocalBranch;
import git4idea.GitRemoteBranch;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import gitflow.ui.AbstractBranchStartDialog;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 *
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */
public class GitflowBranchUtil {

    Project myProject;
    GitRepository myRepo;

    String currentBranchName;
    String branchnameMaster;
    String prefixFeature;
    String prefixRelease;
    String prefixHotfix;

    public GitflowBranchUtil(Project project, GitRepository repo){
        myProject=project;
        myRepo = repo;

        if (repo != null) {
            currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);

            branchnameMaster= GitflowConfigUtil.getMasterBranch(project, repo);
            prefixFeature = GitflowConfigUtil.getFeaturePrefix(project, repo);
            prefixRelease = GitflowConfigUtil.getReleasePrefix(project, repo);
            prefixHotfix = GitflowConfigUtil.getHotfixPrefix(project, repo);
        }
    }

    public boolean hasGitflow(){
        boolean hasGitflow=false;

        hasGitflow = myRepo != null
                       && GitflowConfigUtil.getMasterBranch(myProject, myRepo)!=null
                       && GitflowConfigUtil.getDevelopBranch(myProject, myRepo)!=null
                       && GitflowConfigUtil.getFeaturePrefix(myProject, myRepo)!=null
                       && GitflowConfigUtil.getReleasePrefix(myProject, myRepo)!=null
                       && GitflowConfigUtil.getHotfixPrefix(myProject, myRepo)!=null;

        return hasGitflow;
    }

    public boolean isCurrentBranchMaster(){
        return currentBranchName.startsWith(branchnameMaster);
    }

    public boolean isCurrentBranchFeature(){
        return isBranchFeature(currentBranchName);
    }


    public boolean isCurrentBranchRelease(){
        return currentBranchName.startsWith(prefixRelease);
    }

    public boolean isCurrentBranchHotfix(){
        return isBranchHotfix(currentBranchName);
    }

    //checks whether the current branch also exists on the remote
    public boolean isCurrentBranchPublished(){
        return getRemoteBranchesWithPrefix(currentBranchName).isEmpty()==false;
    }

    public boolean isBranchFeature(String branchName){
        return branchName.startsWith(prefixFeature);
    }

    public boolean isBranchHotfix(String branchName){
        return branchName.startsWith(prefixHotfix);
    }

    //if no prefix specified, returns all remote branches
    public ArrayList<String> getRemoteBranchesWithPrefix(String prefix){
        ArrayList<String> remoteBranches = getRemoteBranchNames();
        ArrayList<String> selectedBranches = new ArrayList<String>();

        for(Iterator<String> i = remoteBranches.iterator(); i.hasNext(); ) {
            String branch = i.next();
            if (branch.contains(prefix)){
                selectedBranches.add(branch);
            }
        }

        return selectedBranches;
    }


    public ArrayList<String> filterBranchListByPrefix(Collection<String> inputBranches,String prefix){
        ArrayList<String> outputBranches= new ArrayList<String>();

        for(Iterator<String> i = inputBranches.iterator(); i.hasNext(); ) {
            String branch = i.next();
            if (branch.contains(prefix)){
                outputBranches.add(branch);
            }
        }

        return outputBranches;
    }

    public ArrayList<String> getRemoteBranchNames(){
        ArrayList<GitRemoteBranch> remoteBranches = new ArrayList<GitRemoteBranch>(myRepo.getBranches().getRemoteBranches());
        ArrayList<String> branchNameList = new ArrayList<String>();

        for(Iterator<GitRemoteBranch> i = remoteBranches.iterator(); i.hasNext(); ) {
            GitRemoteBranch branch = i.next();
            branchNameList.add(branch.getName());
        }

        return branchNameList;
    }


    public ArrayList<String> getLocalBranchNames(){
        ArrayList<GitLocalBranch> localBranches = new ArrayList<GitLocalBranch>(myRepo.getBranches().getLocalBranches());
        ArrayList<String> branchNameList = new ArrayList<String>();

        for(Iterator<GitLocalBranch> i = localBranches.iterator(); i.hasNext(); ) {
            GitLocalBranch branch = i.next();
            branchNameList.add(branch.getName());
        }

        return branchNameList;
    }



    public GitRemote getRemoteByBranch(String branchName){
        GitRemote remote=null;

        ArrayList<GitRemoteBranch> remoteBranches= new ArrayList<GitRemoteBranch>(myRepo.getBranches().getRemoteBranches());

        for(Iterator<GitRemoteBranch> i = remoteBranches.iterator(); i.hasNext(); ) {
            GitRemoteBranch branch = i.next();
            if (branch.getName()==branchName){
                remote=branch.getRemote();
                break;
            }
        }

        return remote;
    }

    public boolean areAllBranchesTracked(String prefix){


        ArrayList<String> localBranches = filterBranchListByPrefix(getLocalBranchNames() , prefix) ;

        //to avoid a vacuous truth value. That is, when no branches at all exist, they shouldn't be
        //considered as "all pulled"
        if (localBranches.isEmpty()){
            return false;
        }

        ArrayList<String> remoteBranches = getRemoteBranchNames();

        //check that every local branch has a matching remote branch
        for(Iterator<String> i = localBranches.iterator(); i.hasNext(); ) {
            String localBranch = i.next();
            boolean hasMatchingRemoteBranch = false;

            for(Iterator<String> j = remoteBranches.iterator(); j.hasNext(); ) {
                String remoteBranch = j.next();

                if (remoteBranch.contains(localBranch)){
                    hasMatchingRemoteBranch=true;
                    break;
                }
            }

            //at least one matching branch wasn't found
            if (hasMatchingRemoteBranch==false){
                return false;
            }
        }

        return true;
    }

    public ComboBoxModel createBranchComboModel(String defaultBranch) {
        final List<String> branchList = this.getLocalBranchNames();
        branchList.remove(defaultBranch);

        ComboEntry[] entries = new ComboEntry[branchList.size() + 1];
        entries[0] = new ComboEntry(defaultBranch, defaultBranch + " (default)");
        for (int i = 1; i <= branchList.size(); i++) {
            String branchName = branchList.get(i - 1);
            entries[i] = new ComboEntry(branchName, branchName);
        }

        return new DefaultComboBoxModel(entries);
    }

    /**
     * Strip a full branch name from its gitflow prefix
     * @param taskFullBranchName full name of the branch (e.g. 'feature/hello');
     * @return the branch name, prefix free (e.g. 'hello')
     */
    public String stripFullBranchName(String taskFullBranchName) {
        if (taskFullBranchName.startsWith(prefixFeature)){
            return taskFullBranchName.substring(prefixFeature.length(), taskFullBranchName.length());
        }
        else if (taskFullBranchName.startsWith(prefixHotfix)){
            return taskFullBranchName.substring(prefixHotfix.length(), taskFullBranchName.length());
        }
        else{
            return null;
        }
    };

    /**
     * An entry for the branch selection dropdown/combo.
     */
    public static class ComboEntry {
        private String branchName, label;

        public ComboEntry(String branchName, String label) {
            this.branchName = branchName;
            this.label = label;
        }

        public String getBranchName() {
            return branchName;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
