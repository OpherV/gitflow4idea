package gitflow;

import com.intellij.openapi.project.Project;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import gitflow.actions.GitflowActions;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class maps repos to their corresponding branch utils
 * Note that the static class is used across projects
 */

public class GitflowBranchUtilManager {
    private static HashMap<GitRepository, GitflowBranchUtil> repoBranchUtilMap;

    @Nullable
    static public GitflowBranchUtil getBranchUtil(@Nullable GitRepository repo){
        if (repo == null) {
            return null;
        }

        return repoBranchUtilMap.get(repo);
    }

    static public void setupBranchUtil(Project project, GitRepository repo){
        GitflowBranchUtil gitflowBranchUtil = new GitflowBranchUtil(project, repo);
        repoBranchUtilMap.put(repo, gitflowBranchUtil);
    }

    /**
     * Repopulates the branchUtils for each repo
     * @param project
     */
    static public void update(Project project){
        if (repoBranchUtilMap == null){
            repoBranchUtilMap = new HashMap<GitRepository, GitflowBranchUtil>();
        }

        List<GitRepository> gitRepositories = GitUtil.getRepositoryManager(project).getRepositories();

        Iterator gitRepositoriesIterator = gitRepositories.iterator();
        while(gitRepositoriesIterator.hasNext()){
            GitRepository repo = (GitRepository) gitRepositoriesIterator.next();
            GitflowBranchUtilManager.setupBranchUtil(project, repo);
        }
    }
}
