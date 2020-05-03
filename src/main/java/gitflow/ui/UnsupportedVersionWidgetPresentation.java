package gitflow.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;

public class UnsupportedVersionWidgetPresentation implements StatusBarWidget.TextPresentation {

	@NotNull
	@Override
	public String getText() {
		return "Unsupported Git Flow Version";
	}

	@Override
	public float getAlignment() {
		return 0;
	}

	@Nullable
	@Override
	public String getTooltipText() {
		return "Click for details";
	}

	@Nullable
	@Override
	public Consumer<MouseEvent> getClickConsumer() {
		return mouseEvent -> {
			MessageDialogBuilder.YesNo builder = MessageDialogBuilder.yesNo("Unsupported Git Flow version", "The Git Flow CLI version installed isn't supported by the Git Flow Integration plugin")
					.yesText("More information (open browser)")
					.noText("no");
			if (builder.show() == Messages.OK) {
				BrowserUtil.browse("https://github.com/OpherV/gitflow4idea/blob/develop/GITFLOW_VERSION.md");
			}
		};

	}
}
