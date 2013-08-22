package git4idea.gitflow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import git4idea.GitUtil;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import git4idea.ui.branch.GitMultiRootBranchConfig;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Opher
 * Date: 8/20/13
 * Time: 1:08 PM
 */
public class BranchUtil {


    public static boolean hasGitflow(Project project){
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);
        String currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);
        boolean hasGitflow=false;
        hasGitflow=(ConfigUtil.getMasterBranch(project)!=null);

        return  hasGitflow;
    }

    public static Boolean isCurrentbranchFeature(Project project){
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);
        String currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);
        String prefixFeature = ConfigUtil.getFeaturePrefix(project);
        return currentBranchName.startsWith(prefixFeature);
    }

    //checks whether the current feature branch also exists on the remote
    public static Boolean isCurrentFeaturePublished(Project project){
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);
        GitRepositoryManager myRepositoryManager = GitUtil.getRepositoryManager(project);
        GitMultiRootBranchConfig myMultiRootBranchConfig  = new GitMultiRootBranchConfig(myRepositoryManager.getRepositories());
        String currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);

        ArrayList<String> remoteBranches = new ArrayList<String>(myMultiRootBranchConfig.getRemoteBranches());

        //get only the branches with the proper prefix
        for(Iterator<String> i = remoteBranches.iterator(); i.hasNext(); ) {
            String item = i.next();
            if (item.contains(currentBranchName)){
                return true;
            }
        }

        return false;
    }
}
