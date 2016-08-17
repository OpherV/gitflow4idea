package gitflow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsTaskHandler;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.impl.TaskManagerImpl;
import com.intellij.tasks.ui.TaskDialogPanel;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowConfigUtil;
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
    private JComboBox featureBaseBranch;
    private JTextField hotfixName;
    private JPanel myPanel;

    private Project myProject;
    private GitRepository myRepo;
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
        featureBaseBranch.setModel(gitflowBranchUtil.createBranchComboModel(defaultFeatureBranch));

        myRepo = GitBranchUtil.getCurrentRepository(project);

        String branchName = myVcsTaskHandler != null
                                            ? myVcsTaskHandler.cleanUpBranchName(myTaskManager.constructDefaultBranchName(task))
                                            : myTaskManager.suggestBranchName(task);

        featureName.setText(branchName);
        featureName.setEditable(false);
        featureName.setEnabled(false);

        hotfixName.setText(branchName);
        hotfixName.setEditable(false);
        hotfixName.setEnabled(false);

        featureBaseBranch.setEnabled(false);

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

        final GitflowBranchUtil.ComboEntry selectedBranch = (GitflowBranchUtil.ComboEntry) featureBaseBranch.getModel().getSelectedItem();

        if (startFeatureRadioButton.isSelected()) {
            final StartFeatureAction startFeatureAction = new StartFeatureAction();
            startFeatureAction.runAction(myProject, selectedBranch.getBranchName(), featureName.getText());
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();

        //disable\enable textfields based on radio selection
        if (e.getStateChange() == ItemEvent.SELECTED && source == noActionRadioButton) {
            featureName.setEditable(false);
            featureName.setEnabled(false);
            featureBaseBranch.setEnabled(false);

            hotfixName.setEditable(false);
            hotfixName.setEnabled(false);
        }
        else if (e.getStateChange() == ItemEvent.SELECTED && source == startFeatureRadioButton) {
            featureName.setEditable(true);
            featureName.setEnabled(true);
            featureBaseBranch.setEnabled(true);

            hotfixName.setEditable(false);
            hotfixName.setEnabled(false);
        }
        else if (e.getStateChange() == ItemEvent.SELECTED && source == startHotfixRadioButton) {
            featureName.setEditable(false);
            featureName.setEnabled(false);
            featureBaseBranch.setEnabled(false);

            hotfixName.setEditable(true);
            hotfixName.setEnabled(true);
        }

    }
}
