package gitflow.ui;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsTaskHandler;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.impl.TaskManagerImpl;
import com.intellij.tasks.ui.TaskDialogPanel;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowConfigUtil;
import gitflow.actions.GitflowAction;
import gitflow.actions.StartFeatureAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class GitflowOpenTaskPanel extends TaskDialogPanel implements ItemListener {
    private JRadioButton noActionRadioButton;
    private JRadioButton startFeatureRadioButton;
    private JRadioButton startHotfixRadioButton;
    private JTextField featureName;
    private JComboBox baseBranch;
    private JTextField hotfixName;
    private JPanel myPanel;

    private Project myProject;
    private GitflowBranchUtil gitflowBranchUtil;
    private TaskManagerImpl myTaskManager;
    private VcsTaskHandler myVcsTaskHandler;



    public GitflowOpenTaskPanel(Project project, Task task){
        myProject = project;
        myTaskManager = (TaskManagerImpl) TaskManager.getManager(project);
        VcsTaskHandler[] vcsTaskHAndlers = VcsTaskHandler.getAllHandlers(project);
        if (vcsTaskHAndlers.length > 0){
            //todo handle case of multiple vcs handlers
            myVcsTaskHandler = vcsTaskHAndlers[0];
        }

        gitflowBranchUtil = new GitflowBranchUtil(project);
        String defaultFeatureBranch = GitflowConfigUtil.getDevelopBranch(project);
        baseBranch.setModel(gitflowBranchUtil.createBranchComboModel(defaultFeatureBranch));

        String branchName = myVcsTaskHandler != null
                                            ? myVcsTaskHandler.cleanUpBranchName(myTaskManager.constructDefaultBranchName(task))
                                            : myTaskManager.suggestBranchName(task);

        featureName.setText(branchName);
        featureName.setEditable(false);
        featureName.setEnabled(false);

        hotfixName.setText(branchName);
        hotfixName.setEditable(false);
        hotfixName.setEnabled(false);

        baseBranch.disable();

        //add listeners
        noActionRadioButton.addItemListener(this);
        startFeatureRadioButton.addItemListener(this);
        startHotfixRadioButton.addItemListener(this);

    }

    @NotNull
    @Override
    public JComponent getPanel() {
        return myPanel;
    }

    @Override
    public void commit() {

        final GitflowBranchUtil.ComboEntry selectedBranch = (GitflowBranchUtil.ComboEntry) baseBranch.getModel().getSelectedItem();

        if (startFeatureRadioButton.isSelected()) {
            final StartFeatureAction startFeatureAction = new StartFeatureAction();
            new com.intellij.openapi.progress.Task.Backgroundable(myProject, "Starting feature " + featureName, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    startFeatureAction.createFeatureBranch(selectedBranch.getBranchName(), featureName.getText());
                }
            }.queue();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();

        //disable\enable textfields based on radio selection
        if (e.getStateChange() == ItemEvent.SELECTED && source == noActionRadioButton) {
            featureName.setEditable(false);
            featureName.setEnabled(false);
            baseBranch.disable();

            hotfixName.setEditable(false);
            hotfixName.setEnabled(false);
        }
        else if (e.getStateChange() == ItemEvent.SELECTED && source == startFeatureRadioButton) {
            featureName.setEditable(true);
            featureName.setEnabled(true);
            baseBranch.enable();

            hotfixName.setEditable(false);
            hotfixName.setEnabled(false);
        }
        else if (e.getStateChange() == ItemEvent.SELECTED && source == startHotfixRadioButton) {
            featureName.setEditable(false);
            featureName.setEnabled(false);
            baseBranch.disable();

            hotfixName.setEditable(true);
            hotfixName.setEnabled(true);
        }

    }
}
