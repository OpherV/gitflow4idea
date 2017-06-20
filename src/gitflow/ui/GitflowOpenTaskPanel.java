package gitflow.ui;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsTaskHandler;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.impl.TaskManagerImpl;
import com.intellij.tasks.ui.TaskDialogPanel;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowBranchUtilManager;
import gitflow.GitflowConfigUtil;
import gitflow.GitflowState;
import gitflow.actions.GitflowAction;
import gitflow.actions.StartFeatureAction;
import gitflow.actions.StartHotfixAction;
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
    private JComboBox hotfixBaseBranch;

    private Project myProject;
    private GitRepository myRepo;
    private GitflowBranchUtil gitflowBranchUtil;
    private TaskManagerImpl myTaskManager;
    private VcsTaskHandler myVcsTaskHandler;
    private Task currentTask;

    private GitflowState gitflowState;


    public GitflowOpenTaskPanel(Project project, Task task){
        myProject = project;
        currentTask = task;
        myTaskManager = (TaskManagerImpl) TaskManager.getManager(project);
        VcsTaskHandler[] vcsTaskHAndlers = VcsTaskHandler.getAllHandlers(project);
        if (vcsTaskHAndlers.length > 0){
            //todo handle case of multiple vcs handlers
            myVcsTaskHandler = vcsTaskHAndlers[0];
        }

        gitflowState = ServiceManager.getService(GitflowState.class);
        gitflowBranchUtil = GitflowBranchUtilManager.getBranchUtil(myRepo);


        String defaultFeatureBranch = GitflowConfigUtil.getDevelopBranch(project, myRepo);
        featureBaseBranch.setModel(gitflowBranchUtil.createBranchComboModel(defaultFeatureBranch));

        String defaultHotfixBranch = GitflowConfigUtil.getMasterBranch(project, myRepo);
        hotfixBaseBranch.setModel(gitflowBranchUtil.createBranchComboModel(defaultHotfixBranch));

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
        hotfixBaseBranch.setEnabled(false);

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

        final GitflowBranchUtil.ComboEntry selectedFeatureBaseBranch = (GitflowBranchUtil.ComboEntry) featureBaseBranch.getModel().getSelectedItem();
        final GitflowBranchUtil.ComboEntry selectedHotfixBaseBranch = (GitflowBranchUtil.ComboEntry) hotfixBaseBranch.getModel().getSelectedItem();

        GitflowAction action;

        if (startFeatureRadioButton.isSelected()) {
            action = new StartFeatureAction();
            action.runAction(myProject, selectedFeatureBaseBranch.getBranchName(), featureName.getText());
            gitflowState.setTaskBranch(currentTask, GitflowConfigUtil.getFeaturePrefix(myProject, myRepo) + featureName.getText());
        }
        else if (startHotfixRadioButton.isSelected()) {
            action =  new StartHotfixAction();
            action.runAction(myProject, selectedHotfixBaseBranch.getBranchName(), hotfixName.getText());
            gitflowState.setTaskBranch(currentTask, GitflowConfigUtil.getHotfixPrefix(myProject, myRepo) + hotfixName.getText());
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
            hotfixBaseBranch.setEnabled(false);
        }
        else if (e.getStateChange() == ItemEvent.SELECTED && source == startFeatureRadioButton) {
            featureName.setEditable(true);
            featureName.setEnabled(true);
            featureBaseBranch.setEnabled(true);

            hotfixName.setEditable(false);
            hotfixName.setEnabled(false);
            hotfixBaseBranch.setEnabled(false);
        }
        else if (e.getStateChange() == ItemEvent.SELECTED && source == startHotfixRadioButton) {
            featureName.setEditable(false);
            featureName.setEnabled(false);
            featureBaseBranch.setEnabled(false);

            hotfixName.setEditable(true);
            hotfixName.setEnabled(true);
            hotfixBaseBranch.setEnabled(true);
        }

    }
}
