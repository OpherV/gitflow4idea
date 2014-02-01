package gitflow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.CollectionComboBoxModel;
import gitflow.GitflowInitOptions;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

/**
 * @author Andreas Vogler (Andreas.Vogler@geneon.de)
 */

public class GitflowInitOptionsDialog extends DialogWrapper {
    private JPanel contentPane;
    private JCheckBox useNonDefaultConfigurationCheckBox;

    private JComboBox productionBranchComboBox;
    private JComboBox developmentBranchComboBox;
    private JTextField featurePrefixTextField;
    private JTextField releasePrefixTextField;
    private JTextField hotfixPrefixTextField;
    private JTextField supportPrefixTextField;
    private JTextField versionPrefixTextField;

    public GitflowInitOptionsDialog(Project project, List<String> localBranches) {
        super(project);

        setTitle("Options for gitflow init");

        productionBranchComboBox.setModel(new CollectionComboBoxModel(localBranches));
        developmentBranchComboBox.setModel(new CollectionComboBoxModel(localBranches));

        init();
        useNonDefaultConfigurationCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                enableFields(e.getStateChange()==ItemEvent.SELECTED);
            }
        });
    }

    private void enableFields(boolean enable) {
        productionBranchComboBox.setEnabled(enable);
        developmentBranchComboBox.setEnabled(enable);
        featurePrefixTextField.setEnabled(enable);
        releasePrefixTextField.setEnabled(enable);
        hotfixPrefixTextField.setEnabled(enable);
        supportPrefixTextField.setEnabled(enable);
        versionPrefixTextField.setEnabled(enable);
    }

    public boolean useNonDefaultConfiguration()
    {
        return useNonDefaultConfigurationCheckBox.isSelected();
    }

    public GitflowInitOptions getOptions()
    {
        GitflowInitOptions options = new GitflowInitOptions();

        options.setUseDefaults(!useNonDefaultConfigurationCheckBox.isSelected());
        options.setProductionBranch((String) productionBranchComboBox.getSelectedItem());
        options.setDevelopmentBranch((String) developmentBranchComboBox.getSelectedItem());
        options.setFeaturePrefix(featurePrefixTextField.getText());
        options.setReleasePrefix(releasePrefixTextField.getText());
        options.setHotfixPrefix(hotfixPrefixTextField.getText());
        options.setSupportPrefix(supportPrefixTextField.getText());
        options.setVersionPrefix(versionPrefixTextField.getText());

        return options;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String message = "Please fill all branch names and prefixes";

        if(useNonDefaultConfiguration()) {
            if(productionBranchComboBox.getSelectedItem().equals(developmentBranchComboBox.getSelectedItem())) {
                return new ValidationInfo("Production and development branch must be distinct branches", developmentBranchComboBox);
            }
            if (StringUtil.isEmptyOrSpaces(featurePrefixTextField.getText())) {
                return new ValidationInfo(message, featurePrefixTextField);
            }
            if (StringUtil.isEmptyOrSpaces(releasePrefixTextField.getText())) {
                return new ValidationInfo(message, releasePrefixTextField);
            }
            if (StringUtil.isEmptyOrSpaces(hotfixPrefixTextField.getText())) {
                return new ValidationInfo(message, hotfixPrefixTextField);
            }
            if (StringUtil.isEmptyOrSpaces(supportPrefixTextField.getText())) {
                return new ValidationInfo(message, supportPrefixTextField);
            }
        }

        return null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
