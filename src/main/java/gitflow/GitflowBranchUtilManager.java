package gitflow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class maps repos to their corresponding branch utils
 * Note that the static class is used across projects
 */

public class GitflowBranchUtilManager {
    private static HashMap<String, GitflowBranchUtil> repoBranchUtilMap;

    static public GitflowBranchUtil getBranchUtil(GitRepository repo){
        if (repo != null && repoBranchUtilMap != null) {
            return repoBranchUtilMap.get(repo.getPresentableUrl());
        } else {
            return null;
        }
    }

    static public void setupBranchUtil(Project project, GitRepository repo){
        GitflowBranchUtil gitflowBranchUtil = new GitflowBranchUtil(project, repo);
        repoBranchUtilMap.put(repo.getPresentableUrl(), gitflowBranchUtil);
        // clean up
        Disposer.register(repo, () -> repoBranchUtilMap.remove(repo));
    }

    /**
     * Repopulates the branchUtils for each repo
     * @param proj
     */
    static public void update(Project proj){
        if (repoBranchUtilMap == null){
            repoBranchUtilMap = new HashMap<String, gitflow.GitflowBranchUtil>();
        }

        List<GitRepository> gitRepositories = GitUtil.getRepositoryManager(proj).getRepositories();

        Iterator gitRepositoriesIterator = gitRepositories.iterator();
        while(gitRepositoriesIterator.hasNext()){
            GitRepository repo = (GitRepository) gitRepositoriesIterator.next();
            GitflowBranchUtilManager.setupBranchUtil(proj, repo);
        }
    }
}
