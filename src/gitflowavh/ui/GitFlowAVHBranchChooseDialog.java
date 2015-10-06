package gitflowavh.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 *  Dialog for choosing branches
 *
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */

public class GitFlowAVHBranchChooseDialog extends DialogWrapper {
    private JPanel contentPane;
    private JList branchList;


    public GitFlowAVHBranchChooseDialog(Project project, List<String> branchNames) {
        super(project, true);

        setModal(true);

        setTitle("Choose Branch");
        branchList.setListData(branchNames.toArray());

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    public String getSelectedBranchName(){
        return branchList.getSelectedValue().toString();
    }
}
