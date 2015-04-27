package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;

import gitflow.ui.GitflowWidget;

public class OpenGitflowPopup extends GitflowAction {

    OpenGitflowPopup() {
        super("Gitflow Operations Popup...");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO calling super will cause a NPE, if no repo is set up. Since we only need the project, we take it directly from the event
        // super.actionPerformed(e);
        Project currentProject = e.getProject();

        GitflowWidget widget = GitflowWidget.findWidgetInstance(currentProject);
        if (widget != null)
            widget.showPopupInCenterOf(WindowManager.getInstance().getFrame(currentProject));
    }

}
