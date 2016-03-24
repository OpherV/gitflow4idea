package gitflowavh.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;

import gitflowavh.ui.GitFlowAVHWidget;

public class OpenGitFlowAVHPopup extends GitFlowAVHAction {

    public OpenGitFlowAVHPopup() {
        super("GitFlowAVH Operations Popup...");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO calling super will cause a NPE, if no repo is set up. Since we only need the project, we take it directly from the event
        // super.actionPerformed(e);
        Project currentProject = e.getProject();

        GitFlowAVHWidget widget = GitFlowAVHWidget.findWidgetInstance(currentProject);
        if (widget != null)
            widget.showPopupInCenterOf(WindowManager.getInstance().getFrame(currentProject));
    }

}
