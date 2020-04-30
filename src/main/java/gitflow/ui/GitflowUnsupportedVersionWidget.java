package gitflow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.impl.status.EditorBasedWidget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	public WidgetPresentation getPresentation() {
		return new UnsupportedVersionWidgetPresentation();
	}
}