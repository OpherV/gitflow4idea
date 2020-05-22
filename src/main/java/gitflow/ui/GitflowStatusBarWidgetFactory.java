package gitflow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class GitflowStatusBarWidgetFactory implements StatusBarWidgetFactory {

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
        GitflowWidget gitflowWidget = new GitflowWidget(project);
        return gitflowWidget;
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget widget) {
        Disposer.dispose(widget);
    }

    @Override
    public boolean canBeEnabledOn(@NotNull StatusBar statusBar) {
        return true;
    }
}
