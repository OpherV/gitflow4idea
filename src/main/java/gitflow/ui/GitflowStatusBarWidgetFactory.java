package gitflow.ui;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.intellij.openapi.wm.WindowManager;
import gitflow.GitflowVersionTester;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class GitflowStatusBarWidgetFactory implements StatusBarWidgetFactory {
    private GitflowWidget myGitflowWidget;

    @NotNull
    @Override
    public String getId() {
        return "gitflowWidget";
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "GitFlow status bar";
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        return true;
    }

    @NotNull
    @Override
    public StatusBarWidget createWidget(@NotNull Project project) {
        if (myGitflowWidget == null) {
            myGitflowWidget = new GitflowWidget(project);
        }
        return myGitflowWidget;
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget widget) {
        Disposer.dispose(widget);
        myGitflowWidget = null;
    }

    @Override
    public boolean canBeEnabledOn(@NotNull StatusBar statusBar) {
        return true;
    }
}
