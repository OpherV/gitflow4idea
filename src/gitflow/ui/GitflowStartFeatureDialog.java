package gitflow.ui;

import com.intellij.openapi.project.Project;

import git4idea.repo.GitRepository;
import gitflow.Gitflow;
import gitflow.GitflowConfigUtil;

public class GitflowStartFeatureDialog extends AbstractBranchStartDialog {

    public GitflowStartFeatureDialog(Gitflow gitflow, GitRepository repository, Project project) {
        super(gitflow, repository, project);
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
