package gitflow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.branch.GitBranchUtil;
import git4idea.config.GitConfigUtil;
import git4idea.repo.GitRepository;
import gitflow.ui.NotifyUtil;

/**
 *
 *
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */

//TODO maybe have this as a singleton instead of static

public class GitflowConfigUtil {

    public static final String BRANCH_MASTER = "gitflow.branch.master";
    public static final String BRANCH_DEVELOP = "gitflow.branch.develop";
    public static final String PREFIX_FEATURE = "gitflow.prefix.feature";
    public static final String PREFIX_RELEASE = "gitflow.prefix.release";
    public static final String PREFIX_HOTFIX = "gitflow.prefix.hotfix";
    public static final String PREFIX_SUPPORT = "gitflow.prefix.support";
    public static final String PREFIX_VERSIONTAG = "gitflow.prefix.versiontag";

    public static String getMasterBranch(Project project, GitRepository repo){
        VirtualFile root = repo.getRoot();

        String masterBranch=null;
        try{
            masterBranch = GitConfigUtil.getValue(project, root, BRANCH_MASTER);
        }
        catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }

        return masterBranch;
    }

    public static String getDevelopBranch(Project project, GitRepository repo){
        VirtualFile root = repo.getRoot();

        String developBranch=null;
        try{
            developBranch = GitConfigUtil.getValue(project, root, BRANCH_DEVELOP);
        }
        catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }

        return developBranch;
    }

    public static String getFeaturePrefix(Project project, GitRepository repo){
        VirtualFile root = repo.getRoot();

        String featurePrefix=null;

        try{
            featurePrefix = GitConfigUtil.getValue(project,root,PREFIX_FEATURE);
        }
        catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
        return featurePrefix;
    }

    public static String getReleasePrefix(Project project, GitRepository repo){
        VirtualFile root = repo.getRoot();

        String releasePrefix=null;

        try{
            releasePrefix = GitConfigUtil.getValue(project,root,PREFIX_RELEASE);
        }
        catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
        return releasePrefix;
    }

    public static String getHotfixPrefix(Project project, GitRepository repo){
        VirtualFile root = repo.getRoot();

        String hotfixPrefix=null;

        try{
            hotfixPrefix = GitConfigUtil.getValue(project,root,PREFIX_HOTFIX);
        }
        catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
        return hotfixPrefix;
    }

    public static String getFeatureNameFromBranch(Project project, GitRepository repo, String branchName){
        String featurePrefix= GitflowConfigUtil.getFeaturePrefix(project, repo);
        return branchName.substring(branchName.indexOf(featurePrefix)+featurePrefix.length(),branchName.length());
    }

    public static String getReleaseNameFromBranch(Project project, GitRepository repo, String branchName){
        String releasePrefix= GitflowConfigUtil.getReleasePrefix(project, repo);
        return branchName.substring(branchName.indexOf(releasePrefix) + releasePrefix.length(), branchName.length());
    }

    public static String getHotfixNameFromBranch(Project project, GitRepository repo, String branchName){
        String hotfixPrefix= GitflowConfigUtil.getHotfixPrefix(project, repo);
        return branchName.substring(branchName.indexOf(hotfixPrefix) + hotfixPrefix.length(), branchName.length());
    }

    public static String getRemoteNameFromBranch(Project project, String branchName){
        return branchName.substring(0,branchName.indexOf("/"));
    }

    public static void setMasterBranch(Project project, GitRepository repo, String branchName)
    {
        VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, BRANCH_MASTER, branchName);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
    }

    public static void setDevelopBranch(Project project, GitRepository repo, String branchName) {
        
        VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, BRANCH_DEVELOP, branchName);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
    }

    public static void setReleasePrefix(Project project, GitRepository repo, String prefix) {
        
        VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, PREFIX_RELEASE, prefix);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
    }

    public static void setFeaturePrefix(Project project, GitRepository repo, String prefix) {
        
        VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, PREFIX_FEATURE, prefix);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
    }

    public static void setHotfixPrefix(Project project, GitRepository repo, String prefix) {

        VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, PREFIX_HOTFIX, prefix);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
    }

    public static void setSupportPrefix(Project project, GitRepository repo, String prefix) {
        
        VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, PREFIX_SUPPORT, prefix);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
    }

    public static void setVersionPrefix(Project project, GitRepository repo, String prefix) {
        
        VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, PREFIX_VERSIONTAG, prefix);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
    }
    
    public static String getBaseBranch(Project project, GitRepository repo, String branchName){
        
        VirtualFile root = repo.getRoot();

        String baseBranch=null;
        try{
            baseBranch = GitConfigUtil.getValue(project, root, "gitflow.branch."+branchName+".base");
        }
        catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }

        return baseBranch;
    }
}
