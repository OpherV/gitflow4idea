package gitflow;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import gitflow.actions.GitflowActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

/**
 * This class maps repos to their corresponding branch utils
 * Note that the static class is used across projects
 */

public class GitflowBranchUtilManager {
    private static HashMap<GitRepository, GitflowBranchUtil> repoBranchUtilMap;

    static public GitflowBranchUtil getBranchUtil(GitRepository repo){
        if (repoBranchUtilMap != null) {
            return repoBranchUtilMap.get(repo);
        } else {
            return null;
        }
    }

    static public void setupBranchUtil(Project project, GitRepository repo){
        GitflowBranchUtil gitflowBranchUtil = new GitflowBranchUtil(project, repo);
        repoBranchUtilMap.put(repo, gitflowBranchUtil);
    }

    /**
     * Repopulates the branchUtils for each repo
     * @param project
     */
    static public void update(Project proj){
        if (repoBranchUtilMap == null){
            repoBranchUtilMap = new HashMap<GitRepository, gitflow.GitflowBranchUtil>();
        }

        List<GitRepository> gitRepositories = GitUtil.getRepositoryManager(proj).getRepositories();

        Iterator gitRepositoriesIterator = gitRepositories.iterator();
        while(gitRepositoriesIterator.hasNext()){
            GitRepository repo = (GitRepository) gitRepositoriesIterator.next();
            GitflowBranchUtilManager.setupBranchUtil(proj, repo);
        }
    }
}
