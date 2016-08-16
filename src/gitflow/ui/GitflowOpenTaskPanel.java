package gitflow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.tasks.Task;
import com.intellij.tasks.ui.TaskDialogPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by opherv on 8/16/16.
 */
public class GitflowOpenTaskPanel extends TaskDialogPanel {
    private JRadioButton startFeatureRadioButton;
    private JTextField featureName;
    private JRadioButton noActionRadioButton;
    private JRadioButton startHotfixRadioButton;
    private JPanel myPanel;
    private JTextField hotfixName;

    public GitflowOpenTaskPanel(Project project, Task task){

    }

    @NotNull
    @Override
    public JComponent getPanel() {
        return myPanel;
    }

    @Override
    public void commit() {

    }
}
