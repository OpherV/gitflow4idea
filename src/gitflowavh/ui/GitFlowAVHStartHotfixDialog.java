package gitflowavh.ui;

import com.intellij.openapi.project.Project;

import gitflowavh.GitFlowAVHConfigUtil;

public class GitFlowAVHStartHotfixDialog extends AbstractBranchStartDialog {

    public GitFlowAVHStartHotfixDialog(Project project) {
        super(project);
    }

    @Override
    protected String getLabel() {
        return "hotfix";
    }

    @Override
    protected String getDefaultBranch() {
        return GitFlowAVHConfigUtil.getMasterBranch(getProject());
    }
}
