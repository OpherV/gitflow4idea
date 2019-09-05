package gitflow;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import gitflow.actions.GitflowPopupGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GitflowMenu extends ActionGroup {
    public GitflowMenu() {
        super("Gitflow", true);
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
        if (anActionEvent == null) {
            return new AnAction[0];
        }

        Project project = anActionEvent.getProject();
        if (project == null) {
            return new AnAction[0];
        }

        GitflowPopupGroup popupGroup = new GitflowPopupGroup(project, true);

        return popupGroup.getActionGroup().getChildren(anActionEvent);
    }
}
