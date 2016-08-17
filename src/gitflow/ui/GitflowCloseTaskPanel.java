package gitflow.ui;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.VcsTaskHandler;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.impl.TaskManagerImpl;
import com.intellij.tasks.ui.TaskDialogPanel;
import git4idea.repo.GitRepository;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowConfigUtil;
import gitflow.GitflowConfigurable;
import gitflow.GitflowState;
import gitflow.actions.FinishFeatureAction;
import gitflow.actions.FinishHotfixAction;
import gitflow.actions.GitflowAction;
import gitflow.actions.StartFeatureAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GitflowCloseTaskPanel extends TaskDialogPanel {
    private JPanel myPanel;
    private JCheckBox finishFeatureCheckbox;
    private JCheckBox finishHotfixCheckbox;
    private JPanel finishHotfixPanel;
    private JTextField tagMessageTextField;
    private JPanel finishFeaturePanel;

    private Project myProject;
    private Task myTask;
    private GitRepository myRepo;
    private GitflowBranchUtil gitflowBranchUtil;
    private TaskManagerImpl myTaskManager;
    private VcsTaskHandler myVcsTaskHandler;
    private String tagMessage;

    final private GitflowState gitflowState;

    GitflowCloseTaskPanel(Project project, Task task){
        myProject = project;
        myTask = task;
        gitflowState = ServiceManager.getService(GitflowState.class);

        gitflowBranchUtil = new GitflowBranchUtil(project);
        myTaskManager = (TaskManagerImpl) TaskManager.getManager(project);
        VcsTaskHandler[] vcsTaskHAndlers = VcsTaskHandler.getAllHandlers(project);
        if (vcsTaskHAndlers.length > 0){
            //todo handle case of multiple vcs handlers
            myVcsTaskHandler = vcsTaskHAndlers[0];
        }


        String branchName = myVcsTaskHandler != null
                ? myVcsTaskHandler.cleanUpBranchName(myTaskManager.constructDefaultBranchName(task))
                : myTaskManager.suggestBranchName(task);

        //TODO same code exists in FinishHotfixAction, make DRYer

        if (GitflowConfigurable.dontTagHotfix(myProject)) {
            tagMessage="";
            tagMessageTextField.setEnabled(false);
            tagMessageTextField.setToolTipText("Hotfix tagging is disabled in Gitflow options");

        }
        else{
            tagMessage = GitflowConfigurable.getCustomHotfixTagCommitMessage(myProject).replace("%name%", branchName);
            tagMessageTextField.setToolTipText(null);
        }

        tagMessageTextField.setText(tagMessage);

    }

    @NotNull
    @Override
    public JComponent getPanel() {
        String taskBranchName = gitflowState.getTaskBranch(myTask);
        if (taskBranchName != null) {
            myPanel.setVisible(true);
            if (gitflowBranchUtil.isBranchFeature(taskBranchName)) {
                finishFeaturePanel.setVisible(true);
                finishHotfixPanel.setVisible(false);
            } else if (gitflowBranchUtil.isBranchHotfix(taskBranchName)) {
                finishFeaturePanel.setVisible(false);
                finishHotfixPanel.setVisible(true);
            }

        }
        else{
            myPanel.setVisible(false);
        }

        return myPanel;
    }

    @Override
    public void commit() {
        String taskFullBranchName = gitflowState.getTaskBranch(myTask);
        String taskBranchName = gitflowBranchUtil.stripFullBranchName(taskFullBranchName);

        if (finishFeatureCheckbox.isEnabled()){
            FinishFeatureAction action = new FinishFeatureAction();
            action.runAction(myProject, taskBranchName);
        }
        else if (finishHotfixCheckbox.isEnabled()){
            FinishHotfixAction action = new FinishHotfixAction();
            action.runAction(myProject, taskBranchName, tagMessageTextField.getText());
        }
    }
}
