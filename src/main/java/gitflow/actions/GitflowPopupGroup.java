package gitflow.actions;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * ActionGroup for the Gitflow popup, constructs the actions for each repo source
 */
public class GitflowPopupGroup {

    Project myProject;
    DefaultActionGroup actionGroup;
    GitRepositoryManager myRepositoryManager;
    List<GitRepository> gitRepositories;
    private AnAction[] children = new AnAction[0];

    public GitflowPopupGroup(@NotNull Project project, boolean includeAdvanced) {
        myProject = project;

        //fetch all the git repositories from the project;
        myRepositoryManager = GitUtil.getRepositoryManager(project);
        gitRepositories = myRepositoryManager.getRepositories();

        createActionGroup(includeAdvanced);
    }

    /**
     * Generates the popup actions for the widget
     */
    private void createActionGroup(boolean includeAdvanced){
        actionGroup = new DefaultActionGroup((String) null, false);

        List<AnAction> actions = new ArrayList<>();

        if (gitRepositories.size() == 1){
            RepoActions repoActions = new RepoActions(myProject, gitRepositories.get(0));
            actions.addAll(repoActions.getRepoActions(includeAdvanced));
            actionGroup.addAll(actions);
        }
        else{
            for (GitRepository repo : gitRepositories){
                RepoActions repoActions = new RepoActions(myProject, repo);
                actions.add(repoActions);
                actionGroup.add(repoActions);
            }
        }

        children = actions.toArray(new AnAction[0]);
    }

    public ActionGroup getActionGroup (){
        return actionGroup;
    }

    /**
     * The child actions of this popup group. Exposed so callers (e.g. the menu)
     * can obtain them without invoking the override-only
     * {@code ActionGroup#getChildren(AnActionEvent)} method.
     */
    public AnAction[] getChildren() {
        return children;
    }
}
