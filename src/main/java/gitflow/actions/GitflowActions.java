package gitflow.actions;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionUiKind;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.project.Project;
import git4idea.actions.GitResolveConflictsAction;
import org.jetbrains.annotations.NotNull;

/**
 * All actions associated with Gitflow
 *
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */
public class GitflowActions {

    public static void runMergeTool(@NotNull Project project) {
        GitResolveConflictsAction resolveAction = new GitResolveConflictsAction();

        // A data context holding just the project is enough to let the
        // conflicts resolver launch.
        DataContext dataContext = SimpleDataContext.builder()
                .add(CommonDataKeys.PROJECT, project)
                .build();

        AnActionEvent event = AnActionEvent.createEvent(
                resolveAction, dataContext, null, ActionPlaces.UNKNOWN, ActionUiKind.NONE, null);
        ActionUtil.performAction(resolveAction, event);
    }
}
