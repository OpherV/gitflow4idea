package gitflow;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import gitflow.ui.GitflowOptionsForm;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Andreas Vogler (Andreas.Vogler@geneon.de)
 * @author Opher Vishnia (opherv@gmail.com)
 */
public class GitflowConfigurable implements Configurable {

    Project project;
    GitflowOptionsForm gitflowOptionsForm;
    PropertiesComponent propertiesComponent;
    Map<Enum<GitflowOptionsFactory.TYPE>, ArrayList<Map<String,String>>> gitflowOptions;

    static GitflowConfigurable instance;

    public GitflowConfigurable(Project project) {
        gitflowOptions = GitflowOptionsFactory.getOptions();
        propertiesComponent = PropertiesComponent.getInstance(project);
        this.project = project;
        instance = this;
    }

    static public GitflowConfigurable getInstance(){
        return instance;
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

    public boolean isOptionActive(String optionId){
        return propertiesComponent.getBoolean(optionId+"_active");
    }

    public String getOptionTextString (String optionId){
        return propertiesComponent.getValue(optionId+"_text");
    }

    @Override
    public boolean isModified() {
        // iterate over branch types (feature/release/hotfix)
        for (GitflowOptionsFactory.TYPE type: GitflowOptionsFactory.TYPE.values()) {
            for (Map<String, String> optionMap : gitflowOptions.get(type)) {
                String optionId = GitflowOptionsFactory.getOptionId(type, optionMap.get("key"));

                boolean isOptionActiveInForm = gitflowOptionsForm.isOptionActive(optionId);
                boolean savedOptionIsActive = propertiesComponent.getBoolean(optionId+"_active");

                if (isOptionActiveInForm != savedOptionIsActive) return true;

                // option has text value
                if (optionMap.get("inputText") != null){
                    String textInForm = gitflowOptionsForm.getOptionText(optionId);
                    String savedOptionText = propertiesComponent.getValue(optionId+"text");
                    if (textInForm.equals(savedOptionText) == false){
                        return true;
                    }
                }

            }
        }

        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        // iterate over branch types (feature/release/hotfix)
        for (GitflowOptionsFactory.TYPE type: GitflowOptionsFactory.TYPE.values()) {
            for (Map<String, String> optionMap : gitflowOptions.get(type)) {
                String optionId = GitflowOptionsFactory.getOptionId(type, optionMap.get("key"));

                // set isActive value
                propertiesComponent.setValue(optionId+"_active",  gitflowOptionsForm.isOptionActive(optionId));

                // set text value, if relevant
                if (optionMap.get("inputText") != null){
                    propertiesComponent.setValue(optionId+"_text",  gitflowOptionsForm.getOptionText(optionId));
                }

            }
        }

    }

    @Override
    public void reset() {
        // iterate over branch types (feature/release/hotfix)
        for (GitflowOptionsFactory.TYPE type: GitflowOptionsFactory.TYPE.values()) {
            for (Map<String, String> optionMap : gitflowOptions.get(type)) {
                String optionId = GitflowOptionsFactory.getOptionId(type, optionMap.get("key"));
                boolean savedOptionIsActive = propertiesComponent.getBoolean(optionId+"_active");
                gitflowOptionsForm.setOptionActive(optionId, savedOptionIsActive);

                // option has text value
                if (optionMap.get("inputText") != null){
                    String textInForm = gitflowOptionsForm.getOptionText(optionId);
                    String savedOptionText = propertiesComponent.getValue(optionId+"text");
                    if (savedOptionText == null){
                        gitflowOptionsForm.setOptionText(optionId, optionMap.get("inputText"));
                    }
                    else{
                        gitflowOptionsForm.setOptionText(optionId, savedOptionText);
                    }

                }

            }
        }
        gitflowOptionsForm.updateFormDisabledStatus();
    }

    @Override
    public void disposeUIResources() {
        gitflowOptionsForm = null;
    }

}
