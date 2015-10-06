package gitflowavh;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.branch.GitBranchUtil;
import git4idea.config.GitConfigUtil;
import git4idea.repo.GitRepository;
import gitflowavh.ui.NotifyUtil;


/**
 * TODO Maybe have this as a singleton instead of static
 */
public class GitFlowAVHConfigUtil {

    public static final String BRANCH_MASTER = "gitflow.branch.master";
    public static final String BRANCH_DEVELOP = "gitflow.branch.develop";
    public static final String PREFIX_FEATURE = "gitflow.prefix.feature";
    public static final String PREFIX_RELEASE = "gitflow.prefix.release";
    public static final String PREFIX_HOTFIX = "gitflow.prefix.hotfix";
    public static final String PREFIX_BUGFIX = "gitflow.prefix.bugfix";
    public static final String PREFIX_SUPPORT = "gitflow.prefix.support";
    public static final String PREFIX_VERSIONTAG = "gitflow.prefix.versiontag";

    /**
     * @param project Project
     * @return String
     */
    public static String getMasterBranch(Project project) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);

        assert repo != null;
        VirtualFile root = repo.getRoot();

        String masterBranch = null;
        try {
            masterBranch = GitConfigUtil.getValue(project, root, BRANCH_MASTER);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }

        return masterBranch;
    }

    /**
     * @param project Project
     * @return String
     */
    public static String getDevelopBranch(Project project) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);

        assert repo != null;
        VirtualFile root = repo.getRoot();

        String developBranch = null;
        try {
            developBranch = GitConfigUtil.getValue(project, root, BRANCH_DEVELOP);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }

        return developBranch;
    }

    /**
     * @param project Project
     * @return String
     */
    public static String getFeaturePrefix(Project project) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);

        assert repo != null;
        VirtualFile root = repo.getRoot();

        String featurePrefix = null;

        try {
            featurePrefix = GitConfigUtil.getValue(project, root, PREFIX_FEATURE);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
        return featurePrefix;
    }

    /**
     * @param project Project
     * @return String
     */
    public static String getReleasePrefix(Project project) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);

        assert repo != null;
        VirtualFile root = repo.getRoot();

        String releasePrefix = null;

        try {
            releasePrefix = GitConfigUtil.getValue(project, root, PREFIX_RELEASE);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
        return releasePrefix;
    }

    /**
     * @param project Project
     * @return String
     */
    public static String getHotfixPrefix(Project project) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);

        assert repo != null;
        VirtualFile root = repo.getRoot();

        String hotfixPrefix = null;

        try {
            hotfixPrefix = GitConfigUtil.getValue(project, root, PREFIX_HOTFIX);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
        return hotfixPrefix;
    }

    /**
     * @param project Project
     * @return String
     */
    public static String getBugfixPrefix(Project project) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);

        assert repo != null;
        VirtualFile root = repo.getRoot();

        String hotfixPrefix = null;

        try {
            hotfixPrefix = GitConfigUtil.getValue(project, root, PREFIX_BUGFIX);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
        return hotfixPrefix;
    }

    /**
     * @param project    Project
     * @param branchName String
     * @return String
     */
    public static String getFeatureNameFromBranch(Project project, String branchName) {
        String featurePrefix = GitFlowAVHConfigUtil.getFeaturePrefix(project);
        return branchName.substring(branchName.indexOf(featurePrefix) + featurePrefix.length(), branchName.length());
    }

    /**
     * @param project    Project
     * @param branchName String
     * @return String
     */
    public static String getReleaseNameFromBranch(Project project, String branchName) {
        String releasePrefix = GitFlowAVHConfigUtil.getReleasePrefix(project);
        return branchName.substring(branchName.indexOf(releasePrefix) + releasePrefix.length(), branchName.length());
    }

    /**
     * @param project    Project
     * @param branchName String
     * @return String
     */
    public static String getHotfixNameFromBranch(Project project, String branchName) {
        String hotfixPrefix = GitFlowAVHConfigUtil.getHotfixPrefix(project);
        return branchName.substring(branchName.indexOf(hotfixPrefix) + hotfixPrefix.length(), branchName.length());
    }

    /**
     * @param project    Project
     * @param branchName String
     * @return String
     */
    public static String getBugfixNameFromBranch(Project project, String branchName) {
        String bugfixPrefix = GitFlowAVHConfigUtil.getBugfixPrefix(project);
        return branchName.substring(branchName.indexOf(bugfixPrefix) + bugfixPrefix.length(), branchName.length());
    }

    /**
     * @param project    Project
     * @param branchName String
     * @return String
     */
    public static String getRemoteNameFromBranch(Project project, String branchName) {
        return branchName.substring(0, branchName.indexOf("/"));
    }

    /**
     * @param project    Project
     * @param branchName String
     */
    public static void setMasterBranch(Project project, String branchName) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);

        assert repo != null;
        VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, BRANCH_MASTER, branchName);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
    }

    /**
     * @param project    Project
     * @param branchName String
     */
    public static void setDevelopBranch(Project project, String branchName) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);

        assert repo != null;
        VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, BRANCH_DEVELOP, branchName);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
    }

    /**
     * @param project Project
     * @param prefix  String
     */
    public static void setReleasePrefix(Project project, String prefix) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);

        assert repo != null;
        VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, PREFIX_RELEASE, prefix);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
    }

    /**
     * @param project Project
     * @param prefix  String
     */
    public static void setFeaturePrefix(Project project, String prefix) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);

        assert repo != null;
        VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, PREFIX_FEATURE, prefix);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
    }

    /**
     * @param project Project
     * @param prefix  String
     */
    public static void setHotfixPrefix(Project project, String prefix) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);

        assert repo != null;
        VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, PREFIX_HOTFIX, prefix);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
    }

    /**
     * @param project Project
     * @param prefix  String
     */
    public static void setBugfixPrefix(Project project, String prefix) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);

        assert repo != null;
        VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, PREFIX_BUGFIX, prefix);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
    }

    /**
     * @param project Project
     * @param prefix  String
     */
    public static void setSupportPrefix(Project project, String prefix) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);

        assert repo != null;
        VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, PREFIX_SUPPORT, prefix);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
    }

    /**
     * @param project Project
     * @param prefix  String
     */
    public static void setVersionPrefix(Project project, String prefix) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);

        assert repo != null;
        VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, PREFIX_VERSIONTAG, prefix);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
    }
}
