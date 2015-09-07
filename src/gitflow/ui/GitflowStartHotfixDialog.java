package gitflow.ui;

import com.intellij.openapi.project.Project;

import git4idea.repo.GitRepository;
import gitflow.Gitflow;
import gitflow.GitflowConfigUtil;

public class GitflowStartHotfixDialog extends AbstractBranchStartDialog {

    public GitflowStartHotfixDialog(Gitflow gitflow, GitRepository repository, Project project) {
        super(gitflow, repository, project);
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
