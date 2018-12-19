package gitflow.ui;

import com.intellij.openapi.project.Project;

import git4idea.repo.GitRepository;
import gitflow.GitflowConfigUtil;

public class GitflowStartFeatureDialog extends AbstractBranchStartDialog {

    public GitflowStartFeatureDialog(Project project, GitRepository repo) {
        super(project, repo);
    }

    @Override
    protected String getLabel() {
        return "feature";
    }

    @Override
    protected String getDefaultBranch() {
        return GitflowConfigUtil.getDevelopBranch(getProject(), myRepo);
    }
}
