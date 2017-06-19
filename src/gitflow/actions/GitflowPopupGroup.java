package gitflow.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.LayeredIcon;
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


        if (gitRepositories.size() == 1){
            RepoActions repoActions = new RepoActions(myProject, gitRepositories.get(0));
            actionGroup.add(repoActions);
        }
        else{
            Iterator gitRepositoriesIterator = gitRepositories.iterator();
            while(gitRepositoriesIterator.hasNext()){
                GitRepository repo = (GitRepository) gitRepositoriesIterator.next();
                RepoActions repoActions = new RepoActions(myProject, repo);
                actionGroup.add(repoActions);
            }
        }
    }

    public ActionGroup getActionGroup (){

        return actionGroup;
    }
}
