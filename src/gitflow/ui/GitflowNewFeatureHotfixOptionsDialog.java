package gitflow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;

import java.util.List;

import javax.swing.*;

import git4idea.repo.GitRepository;
import gitflow.Gitflow;

/**
 * This dialog gets displayed when the user selected the option to create a new feature or hotfix.
 * It prompts the user to enter a name for the new branch and (optionally) to select a branch on
 * which the new branch is based.
 *
 * The default selection for the base branch is <ul> <li>new feature: the development branch</li>
 * <li>new hotfix: the release branch</li> </ul>
 *
 * These are always selected when the dialog is opened to allow the user to skip branch selection,
 * given that this is the common use-case.
 */
public class GitflowNewFeatureHotfixOptionsDialog extends DialogWrapper {

    private JPanel contentPane;
    private JTextField branchNameTextField;
    private JComboBox branchFromCombo;
    private JLabel branchNameLabel;

    private GitRepository repository;
    private Gitflow gitflow;

    /**
     * @param label The label/type of the dialog, for example "hotfix" or "feature". Used for the
     */
    public GitflowNewFeatureHotfixOptionsDialog(Gitflow gitflow, GitRepository repository, Project project, String label) {
        super(project, false);
        this.gitflow = gitflow;
        this.repository = repository;

        init();
        setTitle("New " + label + "...");
        branchNameLabel.setText(String.format("Enter a name for the new %s...", label));
        setModal(true);

        branchFromCombo.setModel(createBranchComboModel());
    }

    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return branchNameTextField;
    }

    private ComboBoxModel<String> createBranchComboModel() {
        List<String> branchList = gitflow.getBranchList(repository);
        String[] branchListArray = branchList.toArray(new String[branchList.size()]);
        return new DefaultComboBoxModel<>(branchListArray);
    }

    @Override
    protected ValidationInfo doValidate() {
        boolean isBranchNameSpecified = branchNameTextField.getText().trim().length() > 0;
        if (!isBranchNameSpecified) {
            return new ValidationInfo("No branch name specified", branchNameTextField);
        } else {
            return null;
        }
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
        return "";
    }
}
