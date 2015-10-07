package gitflowavh.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import gitflowavh.GitFlowAVHBranchUtil;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;


public abstract class AbstractBranchDeleteDialog extends DialogWrapper {
    private JPanel contentPane;
    private JComboBox branchComboBox;
    private JCheckBox forceDeleteCheckBox;
    private JLabel mergeInfoText;
    private JLabel selectBranchLabel;

    protected Project project;
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
        branchComboBox.addItemListener(new ItemChangeListener(this));
        updateMergedInfoText(hasSelectedBranchBeenMerged());
    }

    /**
     * @return The name of the branch that will be deleted
     */
    public String getBranchName() {
        ComboEntry selectedBranch = (ComboEntry) branchComboBox.getModel().getSelectedItem();
        return selectedBranch.getBranchName();
    }

    /**
     * @return The name of the branch that will be deleted, without prefix
     */
    public String getBranchName(boolean withoutPrefix) {
        ComboEntry selectedBranch = (ComboEntry) branchComboBox.getModel().getSelectedItem();
        return selectedBranch.getBranchName().substring(getPrefix().length());
    }

    public boolean isForceDeleteChecked() {
        return forceDeleteCheckBox.isSelected();
    }

    public boolean hasSelectedBranchBeenMerged() {
        return gitFlowAVHBranchUtil.isBranchMerged(getBranchName(), getCheckMergedToBranchName());
    }

    public void updateMergedInfoText(boolean hasBeenMerged) {
        if (hasBeenMerged) {
            mergeInfoText.setText(String.format("Branch has been fully merged to %s.", getCheckMergedToBranchName()));
        } else {
            mergeInfoText.setText(String.format("Branch has not been merged to %s! You need to select force delete to delete this branch.", getCheckMergedToBranchName()));
        }
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

    protected abstract String getPrefix();

    /**
     * @return hasBranchBeenMerged checks agains branch name given by this method
     */
    protected abstract String getCheckMergedToBranchName();

    private ComboBoxModel createBranchComboModel() {
        List<String> branchList = gitFlowAVHBranchUtil.filterBranchListByPrefix(gitFlowAVHBranchUtil.getLocalBranchNames(), getPrefix());

        ComboEntry[] entries = new ComboEntry[branchList.size() + 1];
        for (int i = 0; i < branchList.size(); i++) {
            String branchName = branchList.get(i);
            String branchNameLabel = branchName;
            if (gitFlowAVHBranchUtil.getCurrentBranchName().equals(branchName)) {
                branchNameLabel += " (current)";
            }
            entries[i] = new ComboEntry(branchName, branchNameLabel);

        }
        return new DefaultComboBoxModel(entries);
    }

    /**
     * An entry for the branch selection dropdown/combo.
     */
    protected static class ComboEntry {
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
        private AbstractBranchDeleteDialog deleteDialogClass;


        public ItemChangeListener(AbstractBranchDeleteDialog deleteDialog) {
            deleteDialogClass = deleteDialog;
        }

        @Override
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                deleteDialogClass.updateMergedInfoText(deleteDialogClass.hasSelectedBranchBeenMerged());
            }
        }
    }
}
