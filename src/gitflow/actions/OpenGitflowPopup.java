package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.WindowManager;

import gitflow.ui.GitflowWidget;

public class OpenGitflowPopup extends GitflowAction {

    OpenGitflowPopup() {
        super("Gitflow Operations Popup...");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        GitflowWidget widget = GitflowWidget.findWidgetInstance(myProject);
        if (widget != null)
            widget.showPopupInCenterOf(WindowManager.getInstance().getFrame(myProject));
    }

}
