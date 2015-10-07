package gitflowavh.ui;

import com.intellij.openapi.project.Project;
import gitflowavh.GitFlowAVHConfigUtil;

import java.util.List;


public class GitFlowAVHDeleteBugfixDialog extends AbstractBranchDeleteDialog {
    public GitFlowAVHDeleteBugfixDialog(Project project) {
        super(project);
    }

    protected String getLabel() {
        return "bugfix";
    }

    protected String getPrefix() {
        return GitFlowAVHConfigUtil.getBugfixPrefix(project);
    }

    protected String getCheckMergedToBranchName() {
        return GitFlowAVHConfigUtil.getDevelopBranch(project);
    }
}
