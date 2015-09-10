package gitflow.ui;

import com.intellij.openapi.project.Project;

import gitflow.GitflowConfigUtil;

public class GitflowStartFeatureDialog extends AbstractBranchStartDialog {

    public GitflowStartFeatureDialog(Project project) {
        super(project);
    }

    @Override
    protected String getLabel() {
        return "feature";
    }

    @Override
    protected String getDefaultBranch() {
        return GitflowConfigUtil.getDevelopBranch(getProject());
    }
}
