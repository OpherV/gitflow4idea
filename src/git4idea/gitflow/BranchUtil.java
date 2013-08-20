package git4idea.gitflow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;

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
}
