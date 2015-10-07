package gitflowavh;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import gitflowavh.ui.GitFlowAVHOptionsForm;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


public class GitFlowAVHConfigurable implements Configurable {
    public static final String GITFLOW_FEATURE_FETCH_ORIGIN = "GitFlowAVH.featureFetchOrigin";
    public static final String GITFLOW_FEATURE_KEEP_REMOTE = "GitFlowAVH.featureKeepRemote";

    public static final String GITFLOW_BUGFIX_FETCH_ORIGIN = "GitFlowAVH.bugfixFetchOrigin";
    public static final String GITFLOW_BUGFIX_KEEP_REMOTE = "GitFlowAVH.bugfixKeepRemote";

    public static final String GITFLOW_RELEASE_FETCH_ORIGIN = "GitFlowAVH.releaseFetchOrigin";
    public static final String GITFLOW_DONT_TAG_RELEASE = "GitFlowAVH.dontTagRelease";
    public static final String GITFLOW_PUSH_ON_FINISH_RELEASE = "GitFlowAVH.pushOnFinishRelease";
    public static final String GITFLOW_USE_CUSTOM_TAG_COMMIT_MESSAGE = "GitFlowAVH.useCustomTagCommitMessage";
    public static final String GITFLOW_CUSTOM_TAG_COMMIT_MESSAGE = "GitFlowAVH.customTagCommitMessage";

    public static final String GITFLOW_HOTFIX_FETCH_ORIGIN = "GitFlowAVH.hotfixFetchOrigin";
    public static final String GITFLOW_DONT_TAG_HOTFIX = "GitFlowAVH.dontTagHotfix";
    public static final String GITFLOW_PUSH_ON_FINISH_HOTFIX = "GitFlowAVH.pushOnFinishHotfix";
    public static final String GITFLOW_USE_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE = "GitFlowAVH.useCustomHotfixTagCommitMessage";
    public static final String GITFLOW_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE = "GitFlowAVH.customHotfixTagCommitMessage";

    public static final String DEFAULT_TAG_COMMIT_MESSAGE = "Tagging version %name%";
    public static final String DEFAULT_TAG_HOTFIX_COMMIT_MESSAGE = "Tagging version %name%";

    Project project;
    GitFlowAVHOptionsForm gitflowOptionsForm;


    public GitFlowAVHConfigurable(Project project) {
        this.project = project;
    }

    // Feature

    public static boolean featureFetchOrigin(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(GitFlowAVHConfigurable.GITFLOW_FEATURE_FETCH_ORIGIN, false);
    }

    public static boolean featureKeepRemote(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(GitFlowAVHConfigurable.GITFLOW_FEATURE_KEEP_REMOTE, false);
    }

    // Bugfix

    public static boolean bugfixFetchOrigin(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(GitFlowAVHConfigurable.GITFLOW_BUGFIX_FETCH_ORIGIN, false);
    }

    public static boolean bugfixKeepRemote(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(GitFlowAVHConfigurable.GITFLOW_BUGFIX_KEEP_REMOTE, false);
    }

    // Release

    public static boolean releaseFetchOrigin(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(GitFlowAVHConfigurable.GITFLOW_RELEASE_FETCH_ORIGIN, false);
    }

    public static boolean pushOnReleaseFinish(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(GitFlowAVHConfigurable.GITFLOW_PUSH_ON_FINISH_RELEASE, false);
    }

    public static boolean dontTagRelease(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(GitFlowAVHConfigurable.GITFLOW_DONT_TAG_RELEASE, false);
    }

    // Finish release custom commit message

    public static boolean useCustomTagCommitMessage(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(GitFlowAVHConfigurable.GITFLOW_USE_CUSTOM_TAG_COMMIT_MESSAGE, false);
    }

    public static String getCustomTagCommitMessage(Project project) {
        if (useCustomTagCommitMessage(project)) {
            return PropertiesComponent.getInstance(project).getValue(GitFlowAVHConfigurable.GITFLOW_CUSTOM_TAG_COMMIT_MESSAGE);
        } else {
            return GitFlowAVHConfigurable.DEFAULT_TAG_COMMIT_MESSAGE;
        }
    }

    // Hotfix

    public static boolean hotfixFetchOrigin(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(GitFlowAVHConfigurable.GITFLOW_HOTFIX_FETCH_ORIGIN, false);
    }

    public static boolean pushOnHotfixFinish(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(GitFlowAVHConfigurable.GITFLOW_PUSH_ON_FINISH_HOTFIX, false);
    }

    public static boolean dontTagHotfix(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(GitFlowAVHConfigurable.GITFLOW_DONT_TAG_HOTFIX, false);
    }

    // Finish hotfix custom commit message

    public static boolean useCustomHotfixTagCommitMessage(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(GitFlowAVHConfigurable.GITFLOW_USE_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE, false);
    }

    public static String getCustomHotfixTagCommitMessage(Project project) {
        if (useCustomHotfixTagCommitMessage(project)) {
            return PropertiesComponent.getInstance(project).getValue(GitFlowAVHConfigurable.GITFLOW_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE);
        } else {
            return GitFlowAVHConfigurable.DEFAULT_TAG_HOTFIX_COMMIT_MESSAGE;
        }
    }


    @Override
    public String getDisplayName() {
        return "GitFlowAVH";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        gitflowOptionsForm = new GitFlowAVHOptionsForm();
        return gitflowOptionsForm.getContentPane();
    }

    @Override
    public boolean isModified() {
        return PropertiesComponent.getInstance(project).getBoolean(GITFLOW_FEATURE_FETCH_ORIGIN, false) != gitflowOptionsForm.isFeatureFetchOrigin() ||
                PropertiesComponent.getInstance(project).getBoolean(GITFLOW_FEATURE_KEEP_REMOTE, false) != gitflowOptionsForm.isFeatureKeepRemote() ||

                PropertiesComponent.getInstance(project).getBoolean(GITFLOW_RELEASE_FETCH_ORIGIN, false) != gitflowOptionsForm.isReleaseFetchOrigin() ||
                PropertiesComponent.getInstance(project).getBoolean(GITFLOW_PUSH_ON_FINISH_RELEASE, false) != gitflowOptionsForm.isPushOnFinishRelease() ||
                PropertiesComponent.getInstance(project).getBoolean(GITFLOW_DONT_TAG_RELEASE, false) != gitflowOptionsForm.isDontTagRelease() ||
                PropertiesComponent.getInstance(project).getBoolean(GITFLOW_USE_CUSTOM_TAG_COMMIT_MESSAGE, false) != gitflowOptionsForm.isUseCustomTagCommitMessage() ||
                !PropertiesComponent.getInstance(project).getValue(GITFLOW_CUSTOM_TAG_COMMIT_MESSAGE, DEFAULT_TAG_COMMIT_MESSAGE).equals(gitflowOptionsForm.getCustomTagCommitMessage()) ||

                PropertiesComponent.getInstance(project).getBoolean(GITFLOW_HOTFIX_FETCH_ORIGIN, false) != gitflowOptionsForm.isHotfixFetchOrigin() ||
                PropertiesComponent.getInstance(project).getBoolean(GITFLOW_PUSH_ON_FINISH_HOTFIX, false) != gitflowOptionsForm.isPushOnFinishHotfix() ||

                PropertiesComponent.getInstance(project).getBoolean(GITFLOW_BUGFIX_FETCH_ORIGIN, false) != gitflowOptionsForm.isBugfixFetchOrigin() ||
                PropertiesComponent.getInstance(project).getBoolean(GITFLOW_BUGFIX_KEEP_REMOTE, false) != gitflowOptionsForm.isBugfixKeepRemote() ||

                PropertiesComponent.getInstance(project).getBoolean(GITFLOW_DONT_TAG_HOTFIX, false) != gitflowOptionsForm.isDontTagHotfix() ||
                PropertiesComponent.getInstance(project).getBoolean(GITFLOW_USE_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE, false) != gitflowOptionsForm.isUseCustomHotfixComitMessage() ||
                !PropertiesComponent.getInstance(project).getValue(GITFLOW_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE, DEFAULT_TAG_HOTFIX_COMMIT_MESSAGE).equals(gitflowOptionsForm.getCustomHotfixCommitMessage())
                ;
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent.getInstance(project).setValue(GITFLOW_FEATURE_FETCH_ORIGIN, Boolean.toString(gitflowOptionsForm.isFeatureFetchOrigin()));
        PropertiesComponent.getInstance(project).setValue(GITFLOW_FEATURE_KEEP_REMOTE, Boolean.toString(gitflowOptionsForm.isFeatureKeepRemote()));

        PropertiesComponent.getInstance(project).setValue(GITFLOW_RELEASE_FETCH_ORIGIN, Boolean.toString(gitflowOptionsForm.isReleaseFetchOrigin()));
        PropertiesComponent.getInstance(project).setValue(GITFLOW_PUSH_ON_FINISH_RELEASE, Boolean.toString(gitflowOptionsForm.isPushOnFinishRelease()));
        PropertiesComponent.getInstance(project).setValue(GITFLOW_DONT_TAG_RELEASE, Boolean.toString(gitflowOptionsForm.isDontTagRelease()));
        PropertiesComponent.getInstance(project).setValue(GITFLOW_USE_CUSTOM_TAG_COMMIT_MESSAGE, Boolean.toString(gitflowOptionsForm.isUseCustomTagCommitMessage()));
        PropertiesComponent.getInstance(project).setValue(GITFLOW_CUSTOM_TAG_COMMIT_MESSAGE, gitflowOptionsForm.getCustomTagCommitMessage());

        PropertiesComponent.getInstance(project).setValue(GITFLOW_HOTFIX_FETCH_ORIGIN, Boolean.toString(gitflowOptionsForm.isHotfixFetchOrigin()));
        PropertiesComponent.getInstance(project).setValue(GITFLOW_PUSH_ON_FINISH_HOTFIX, Boolean.toString(gitflowOptionsForm.isPushOnFinishHotfix()));

        PropertiesComponent.getInstance(project).setValue(GITFLOW_BUGFIX_FETCH_ORIGIN, Boolean.toString(gitflowOptionsForm.isBugfixFetchOrigin()));
        PropertiesComponent.getInstance(project).setValue(GITFLOW_BUGFIX_KEEP_REMOTE, Boolean.toString(gitflowOptionsForm.isBugfixKeepRemote()));

        PropertiesComponent.getInstance(project).setValue(GITFLOW_DONT_TAG_HOTFIX, Boolean.toString(gitflowOptionsForm.isDontTagHotfix()));
        PropertiesComponent.getInstance(project).setValue(GITFLOW_USE_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE, Boolean.toString(gitflowOptionsForm.isUseCustomHotfixComitMessage()));
        PropertiesComponent.getInstance(project).setValue(GITFLOW_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE, gitflowOptionsForm.getCustomHotfixCommitMessage());
    }

    @Override
    public void reset() {
        gitflowOptionsForm.setFeatureFetchOrigin(PropertiesComponent.getInstance(project).getBoolean(GITFLOW_FEATURE_FETCH_ORIGIN, false));
        gitflowOptionsForm.setFeatureKeepRemote(PropertiesComponent.getInstance(project).getBoolean(GITFLOW_FEATURE_KEEP_REMOTE, false));

        gitflowOptionsForm.setReleaseFetchOrigin(PropertiesComponent.getInstance(project).getBoolean(GITFLOW_RELEASE_FETCH_ORIGIN, false));
        gitflowOptionsForm.setPushOnFinishRelease(PropertiesComponent.getInstance(project).getBoolean(GITFLOW_PUSH_ON_FINISH_RELEASE, false));
        gitflowOptionsForm.setDontTagRelease(PropertiesComponent.getInstance(project).getBoolean(GITFLOW_DONT_TAG_RELEASE, false));
        gitflowOptionsForm.setUseCustomTagCommitMessage(PropertiesComponent.getInstance(project).getBoolean(GITFLOW_USE_CUSTOM_TAG_COMMIT_MESSAGE, false));
        gitflowOptionsForm.setCustomTagCommitMessage(PropertiesComponent.getInstance(project).getValue(GITFLOW_CUSTOM_TAG_COMMIT_MESSAGE, DEFAULT_TAG_COMMIT_MESSAGE));

        gitflowOptionsForm.setHotfixFetchOrigin(PropertiesComponent.getInstance(project).getBoolean(GITFLOW_HOTFIX_FETCH_ORIGIN, false));
        gitflowOptionsForm.setPushOnFinishHotfix(PropertiesComponent.getInstance(project).getBoolean(GITFLOW_PUSH_ON_FINISH_HOTFIX, false));

        gitflowOptionsForm.setBugfixFetchOrigin(PropertiesComponent.getInstance(project).getBoolean(GITFLOW_BUGFIX_FETCH_ORIGIN, false));
        gitflowOptionsForm.setBugfixKeepRemote(PropertiesComponent.getInstance(project).getBoolean(GITFLOW_BUGFIX_KEEP_REMOTE, false));

        gitflowOptionsForm.setDontTagHotfix(PropertiesComponent.getInstance(project).getBoolean(GITFLOW_DONT_TAG_HOTFIX, false));
        gitflowOptionsForm.setUseCustomHotfixCommitMessage(PropertiesComponent.getInstance(project).getBoolean(GITFLOW_USE_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE, false));
        gitflowOptionsForm.setCustomHotfixCommitMessage(PropertiesComponent.getInstance(project).getValue(GITFLOW_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE, DEFAULT_TAG_HOTFIX_COMMIT_MESSAGE));
    }

    @Override
    public void disposeUIResources() {
        gitflowOptionsForm = null;
    }
}
