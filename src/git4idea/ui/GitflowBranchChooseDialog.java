package git4idea.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class GitflowBranchChooseDialog extends DialogWrapper {
    private JPanel contentPane;
    private JList branchList;


    public GitflowBranchChooseDialog(Project project, List<String> branchList) {
        super(project, true);

        setModal(true);

        setTitle("Choose Branch");
        branchList.addAll(branchList);

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
