package git4idea.gitflow;

import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;
import git4idea.branch.GitBranchUtil;
import git4idea.config.GitConfigUtil;
import git4idea.repo.GitRepository;

/**
 * Created with IntelliJ IDEA.
 * User: Opher
 * Date: 8/20/13
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigUtil {

    public static final String BRANCH_MASTER = "gitflow.branch.master";
    public static final String BRANCH_DEVELOP = "gitflow.branch.develop";
    public static final String PREFIX_FEATURE = "gitflow.prefix.feature";
    public static final String PREFIX_RELEASE = "gitflow.prefix.release";
    public static final String PREFIX_HOTFIX = "gitflow.prefix.hotfix";
    public static final String PREFIX_VERSIONTAG = "gitflow.prefix.versiontag";

    public static String getMasterBranch(Project project){
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);
        VirtualFile root = repo.getRoot();


        String masterBranch=null;
        try{
            masterBranch = GitConfigUtil.getValue(project, root, BRANCH_MASTER);
        }
        catch (VcsException e) {

        }

        return masterBranch;
    }

    public static String getFeaturePrefix(Project project){
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);
        VirtualFile root = repo.getRoot();

        String featurePrefix=null;

        try{
            featurePrefix = GitConfigUtil.getValue(project,root,PREFIX_FEATURE);
        }
        catch (VcsException e) {

        }
        return featurePrefix;
    }

    public static String getFeatureNameFromBranch(Project project, String branchName){
        String featurePrefix=ConfigUtil.getFeaturePrefix(project);
        return branchName.substring(branchName.indexOf(featurePrefix)+featurePrefix.length(),branchName.length());
    }


    public static String getRemoteNameFromBranch(Project project, String branchName){
        return branchName.substring(0,branchName.indexOf("/"));
    }



}
