package gitflow.actions;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import git4idea.GitUtil;

import java.util.Iterator;
import java.util.List;

/**
 * ActionGroup for the Gitflow popup, constructs the actions for each repo source
 */
public class GitflowPopupGroup {

    Project myProject;
    DefaultActionGroup actionGroup;
    GitRepositoryManager myRepositoryManager;
    List<GitRepository> gitRepositories;

    public GitflowPopupGroup(@NotNull Project project) {
        myProject = project;

        //fetch all the git repositories from the project;
        myRepositoryManager = GitUtil.getRepositoryManager(project);
        gitRepositories = myRepositoryManager.getRepositories();

        createActionGroup();
    }

    /**
     * Generates the popup actions for the widget
     */
    private void createActionGroup(){
        actionGroup = new DefaultActionGroup(null, false);

        GitflowActions actions = new GitflowActions(myProject);

        Iterator gitRepositoriesIterator = gitRepositories.iterator();
        while(gitRepositoriesIterator.hasNext()){
            GitRepository gitRepository = (GitRepository) gitRepositoriesIterator.next();
            String repoName = gitRepository.getRoot().toString();
            actionGroup.addSeparator(repoName);
            ActionGroup repoActions = actions.getActionsForRepo(gitRepository);
            actionGroup.addAll(repoActions);
        }

    }

    public ActionGroup getActionGroup (){

        return actionGroup;
    }
}
