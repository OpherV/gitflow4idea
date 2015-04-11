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

        GitflowWidget widget = (GitflowWidget) WindowManager.getInstance().getStatusBar(myProject).getWidget(GitflowWidget.getWidgetID());
        widget.getPopupStep().show(WindowManager.getInstance().findVisibleFrame());
    }
}
