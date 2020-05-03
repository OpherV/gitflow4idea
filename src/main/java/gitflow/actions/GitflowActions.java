package gitflow.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import git4idea.actions.GitResolveConflictsAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * All actions associated with Gitflow
 *
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */
public class GitflowActions {

    public static void runMergeTool(Project project){
        GitResolveConflictsAction resolveAction = new GitResolveConflictsAction();
        AnActionEvent e = new AnActionEvent(null, new ProjectDataContext(project), ActionPlaces.UNKNOWN, new Presentation(""), ActionManager.getInstance(), 0);
        resolveAction.actionPerformed(e);
    }


    /**
     * Simple wrapper containing just enough to let the conflicts resolver to launch
     * We could have transferred the DataContext or wrapped a HackyDataContext from the previous action,
     * but that would make the semantics terrible
     */
    private final static class ProjectDataContext implements DataContext {
        private Project project;

        private ProjectDataContext(Project project) {
            this.project = project;
        }

        @Nullable
        @Override
        public Object getData(@NotNull String dataId) {
            if(CommonDataKeys.PROJECT.getName().equals(dataId)) {
                return project;
            } else {
                throw new UnsupportedOperationException(dataId);
            }
        }
    }
}
