package gitflow.ui;

import com.intellij.openapi.project.Project;

import gitflow.GitflowConfigUtil;

public class GitflowStartHotfixDialog extends AbstractBranchStartDialog {

    public GitflowStartHotfixDialog(Project project) {
        super(project);
    }

    @Override
    protected String getLabel() {
        return "hotfix";
    }

    @Override
    protected String getDefaultBranch() {
        return GitflowConfigUtil.getMasterBranch(getProject());
    }
}
