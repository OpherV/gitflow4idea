package gitflowavh.ui;

import com.intellij.openapi.project.Project;

import gitflowavh.GitFlowAVHConfigUtil;

public class GitFlowAVHStartBugfixDialog extends AbstractBranchStartDialog {

    public GitFlowAVHStartBugfixDialog(Project project) {
        super(project);
    }

    @Override
    protected String getLabel() {
        return "bugfix";
    }

    @Override
    protected String getDefaultBranch() {
        return GitFlowAVHConfigUtil.getDevelopBranch(getProject());
    }
}
