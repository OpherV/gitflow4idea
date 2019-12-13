package gitflow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidget.TextPresentation;
import com.intellij.openapi.wm.impl.status.EditorBasedWidget;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;

public class GitflowUnsupportedVersionWidget extends EditorBasedWidget {

	public GitflowUnsupportedVersionWidget(@NotNull Project project) {
		super(project);
	}

	@NotNull
	@Override
	public String ID() {
		return "GitflowUnsupportedVersionWidget";
	}

	@Nullable
	@Override
	public WidgetPresentation getPresentation(@NotNull PlatformType type) {
		return new UnsupportedVersionWidgetPresentation();
	}
}