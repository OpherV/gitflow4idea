package gitflowavh.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import gitflowavh.GitFlowAVHBranchUtil;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public abstract class AbstractBranchDeleteDialog extends DialogWrapper {
    private JPanel contentPane;
    private JComboBox branchComboBox;
    private JCheckBox forceDeleteCheckBox;
    private JLabel mergeInfoText;
    private JLabel selectBranchLabel;

    private Project project;
    protected GitFlowAVHBranchUtil gitFlowAVHBranchUtil;


    public AbstractBranchDeleteDialog(Project project) {
        super(project, false);
        this.project = project;
        this.gitFlowAVHBranchUtil = new GitFlowAVHBranchUtil(project);

        init();
        final String label = getLabel();
        setTitle("Delete " + label + " branch...");
        setModal(true);

        branchComboBox.setModel(createBranchComboModel());
        branchComboBox.addItemListener(new ItemChangeListener());
    }

    /**
     * @return The name of the branch that will be deleted
     */
    public String getBranchName() {
        ComboEntry selectedBranch = (ComboEntry) branchComboBox.getModel().getSelectedItem();
        return selectedBranch.getBranchName();
    }

    public boolean isForceDeleteChecked() {
        return forceDeleteCheckBox.isSelected();
    }

    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    protected Project getProject() {
        return this.project;
    }

    /**
     * @return The label for this dialog (e.g. "hotfix" or "feature"). Will be used for the window
     * title and other labels.
     */
    protected abstract String getLabel();

    protected abstract ComboBoxModel createBranchComboModel();

    protected abstract boolean isSelectedBranchMerged();

    /**
     * An entry for the branch selection dropdown/combo.
     */
    private static class ComboEntry {
        private String branchName, label;

        public ComboEntry(String branchName, String label) {
            this.branchName = branchName;
            this.label = label;
        }

        public String getBranchName() {
            return branchName;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /**
     * Branch selection change listener. This allows us to show merge status of the branch immediately.
     */
    private class ItemChangeListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Object item = event.getItem();
                // Do something with object

            }
        }
    }
}
