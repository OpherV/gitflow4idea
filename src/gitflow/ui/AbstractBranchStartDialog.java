package gitflow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;

import java.util.Collections;
import java.util.List;

import javax.swing.*;

import git4idea.repo.GitRepository;
import gitflow.Gitflow;

/**
 * Base class for a "start" dialog. Prompts the user to enter a name for a new branch and select a
 * base branch. See {@link GitflowStartFeatureDialog} for an example implementation.
 */
public abstract class AbstractBranchStartDialog extends DialogWrapper {

    private JPanel contentPane;
    private JTextField branchNameTextField;
    private JComboBox branchFromCombo;
    private JLabel branchNameLabel;

    private GitRepository repository;
    private Gitflow gitflow;
    private Project project;

    public AbstractBranchStartDialog(Gitflow gitflow, GitRepository repository, Project project) {
        super(project, false);
        this.gitflow = gitflow;
        this.repository = repository;
        this.project = project;

        init();
        final String label = getLabel();
        setTitle("New " + label + "...");
        branchNameLabel.setText(String.format("Enter a name for the new %s...", label));
        setModal(true);

        branchFromCombo.setModel(createBranchComboModel());
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return branchNameTextField;
    }

    /**
     * @return The name of the new branch as specified by the user
     */
    public String getNewBranchName() {
        return branchNameTextField.getText().trim();
    }

    /**
     * @return The name of the base branch (the branch on which the new hotfix or feature should be
     * based on)
     */
    public String getBaseBranchName() {
        return (String) branchFromCombo.getModel().getSelectedItem();
    }

    /**
     * @return The label for this dialog (e.g. "hotfix" or "feature"). Will be used for the window
     * title and other labels.
     */
    protected abstract String getLabel();

    /**
     * @return The name of the default branch, i.e. the branch that is selected by default when
     * opening the dialog.
     */
    protected abstract String getDefaultBranch();

    protected Project getProject() {
        return this.project;
    }

    @Override
    protected ValidationInfo doValidate() {
        boolean isBranchNameSpecified = branchNameTextField.getText().trim().length() > 0;
        if (!isBranchNameSpecified) {
            return new ValidationInfo("No name specified", branchNameTextField);
        } else {
            return null;
        }
    }

    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    private ComboBoxModel<String> createBranchComboModel() {
        List<String> branchList = gitflow.getBranchList(repository);

        final String defaultBranch = getDefaultBranch();
        branchList.remove(defaultBranch);
        Collections.sort(branchList);
        branchList.add(0, defaultBranch);

        String[] branchListArray = branchList.toArray(new String[branchList.size()]);
        return new DefaultComboBoxModel<>(branchListArray);
    }
}
