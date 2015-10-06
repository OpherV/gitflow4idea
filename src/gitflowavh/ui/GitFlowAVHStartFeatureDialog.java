package gitflowavh.ui;

import com.intellij.openapi.project.Project;

import gitflowavh.GitFlowAVHConfigUtil;

public class GitFlowAVHStartFeatureDialog extends AbstractBranchStartDialog {

    public GitFlowAVHStartFeatureDialog(Project project) {
        super(project);
    }

    @Override
    protected String getLabel() {
        return "feature";
    }

    @Override
    protected String getDefaultBranch() {
        return GitFlowAVHConfigUtil.getDevelopBranch(getProject());
    }
}
