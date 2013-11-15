package gitflow;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import gitflow.ui.GitflowOptionsForm;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Andreas Vogler (Andreas.Vogler@geneon.de)
 */

public class GitflowConfigurable implements Configurable {
    public static final String GITFLOW_PUSH_ON_FINISH_RELEASE = "Gitflow.pushOnFinishRelease";
    Project project;
    GitflowOptionsForm gitflowOptionsForm;


    public GitflowConfigurable(Project project)
    {
        this.project = project;
    }

    @Override
    public String getDisplayName() {
        return "Gitflow";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        gitflowOptionsForm = new GitflowOptionsForm();
        return gitflowOptionsForm.getContentPane();
    }

    @Override
    public boolean isModified() {
        return PropertiesComponent.getInstance(project).getBoolean(GITFLOW_PUSH_ON_FINISH_RELEASE, false) != gitflowOptionsForm.isPushOnFinishRelease();
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent.getInstance(project).setValue(GITFLOW_PUSH_ON_FINISH_RELEASE, Boolean.toString(gitflowOptionsForm.isPushOnFinishRelease()));
    }

    @Override
    public void reset() {
        gitflowOptionsForm.setPushOnFinishRelease(PropertiesComponent.getInstance(project).getBoolean(GITFLOW_PUSH_ON_FINISH_RELEASE, false));
    }

    @Override
    public void disposeUIResources() {
        gitflowOptionsForm = null;
    }
}
