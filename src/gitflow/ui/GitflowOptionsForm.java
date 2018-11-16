package gitflow.ui;

import gitflow.GitflowOptionsFactory;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andreas Vogler (Andreas.Vogler@geneon.de)
 * @author Opher Vishnia (opherv@gmail.com)
 */
public class GitflowOptionsForm  implements ItemListener {

    private JPanel contentPane;
    private JPanel releasePanel;
    private JPanel featurePanel;
    private JPanel hotfixPanel;
    private JPanel bugfixPanel;

    Map<Enum<GitflowOptionsFactory.TYPE>, ArrayList<Map<String,String>>> gitflowOptions;
    Map<String, OptionComponent> optionComponents;

    private class OptionComponent{
        public JComponent checkbox;
        public JComponent textfield;

        public OptionComponent(JComponent checkboxComponent, @Nullable JComponent textfieldComponent){
            this.checkbox = checkboxComponent;
            this.textfield = textfieldComponent;
        }
    }

    public GitflowOptionsForm(){
        gitflowOptions = GitflowOptionsFactory.getOptions();

        optionComponents = new HashMap<String,OptionComponent>();
        featurePanel.setLayout(new BoxLayout(featurePanel, BoxLayout.Y_AXIS));
        hotfixPanel.setLayout(new BoxLayout(hotfixPanel, BoxLayout.Y_AXIS));
        releasePanel.setLayout(new BoxLayout(releasePanel, BoxLayout.Y_AXIS));
        bugfixPanel.setLayout(new BoxLayout(featurePanel, BoxLayout.Y_AXIS));

        HashMap<GitflowOptionsFactory.TYPE, JPanel> branchTypeToPanel = new HashMap<GitflowOptionsFactory.TYPE, JPanel>();

        branchTypeToPanel.put(GitflowOptionsFactory.TYPE.FEATURE, featurePanel);
        branchTypeToPanel.put(GitflowOptionsFactory.TYPE.RELEASE, releasePanel);
        branchTypeToPanel.put(GitflowOptionsFactory.TYPE.HOTFIX, hotfixPanel);
        branchTypeToPanel.put(GitflowOptionsFactory.TYPE.BUGFIX, bugfixPanel);

        for (GitflowOptionsFactory.TYPE type: GitflowOptionsFactory.TYPE.values()) {
            JPanel optionPanel = branchTypeToPanel.get(type);

            for (Map<String, String> optionMap : gitflowOptions.get(type)) {
                JPanel optionRow = new JPanel();
                optionRow.setLayout(new BoxLayout(optionRow, BoxLayout.X_AXIS));
                optionRow.setAlignmentX(Component.LEFT_ALIGNMENT);

                JCheckBox checkbox = new JCheckBox();
                String checkboxText = optionMap.get("description");
                if (optionMap.get("flag") != null){
                    checkboxText += " (" + optionMap.get("flag") + ")";
                }
                checkbox.setText(checkboxText);
                checkbox.setMargin(new Insets(0, 0, 0, 20));
                checkbox.addItemListener(this);
                optionRow.add(checkbox);

                JTextField textField = null;

                // some options have input text
                if (optionMap.get("inputText") != null) {
                    textField = new JTextField();
                    textField.setText(optionMap.get("inputText"));
                    textField.setToolTipText(optionMap.get("toolTip"));
                    optionRow.add(textField);
                }

                optionPanel.add(optionRow);

                // keep a reference for the components for future use
                OptionComponent optionComponent = new OptionComponent(checkbox, textField);
                optionComponents.put(GitflowOptionsFactory.getOptionId(type, optionMap.get("key")), optionComponent);
            }
        }
    }


    public JPanel getContentPane() {
        return contentPane;
    }

    /** Listens to the check boxes. */
    public void itemStateChanged(ItemEvent e) {
        this.updateFormDisabledStatus();
    }

    public void updateFormDisabledStatus(){
        JCheckBox dontTagRelease = (JCheckBox) optionComponents.get("RELEASE_dontTag").checkbox;
        JCheckBox customTagCommitMessageCheckbox = (JCheckBox) optionComponents.get("RELEASE_customTagCommitMessage").checkbox;
        JTextField customTagCommitMessageTexfield = (JTextField) optionComponents.get("RELEASE_customTagCommitMessage").textfield;

        JCheckBox dontTagHotfix = (JCheckBox) optionComponents.get("HOTFIX_dontTag").checkbox;
        JCheckBox customHotfixCommitMessageCheckbox = (JCheckBox) optionComponents.get("HOTFIX_customHotfixCommitMessage").checkbox;
        JTextField customHotfixCommitMessageTextfield = (JTextField) optionComponents.get("HOTFIX_customHotfixCommitMessage").textfield;

        //disable\enable the finish release tag commit message according to the checkbox state

        if (customTagCommitMessageCheckbox.isSelected() && dontTagRelease.isSelected()==false) {
            customTagCommitMessageTexfield.setEditable(true);
            customTagCommitMessageTexfield.setEnabled(true);
        }
        else{
            customTagCommitMessageTexfield.setEditable(false);
        }


        if (dontTagRelease.isSelected()) {
            customTagCommitMessageCheckbox.setEnabled(false);
            customTagCommitMessageTexfield.setEnabled(false);
        }
        else{
            customTagCommitMessageCheckbox.setEnabled(true);
            if( customTagCommitMessageCheckbox.isSelected()){
                customTagCommitMessageTexfield.setEnabled(true);
                customTagCommitMessageTexfield.setEditable(true);
            }
        }

        //disable\enable the finish hotfix tag commit message according to the checkbox state
        if (customHotfixCommitMessageCheckbox.isSelected()) {
            customHotfixCommitMessageTextfield.setEditable(true);
            customHotfixCommitMessageTextfield.setEnabled(true);
        }
        else{
            customHotfixCommitMessageTextfield.setEditable(false);
        }

        if (dontTagHotfix.isSelected()) {
            customHotfixCommitMessageCheckbox.setEnabled(false);
            customHotfixCommitMessageTextfield.setEnabled(false);
        }
        else{
            customHotfixCommitMessageCheckbox.setEnabled(true);
            if( customHotfixCommitMessageCheckbox.isSelected()){
                customHotfixCommitMessageTextfield.setEnabled(true);
                customHotfixCommitMessageTextfield.setEditable(true);
            }
        }
    }

    public boolean isOptionActive(String optionId){
        return ((JCheckBox) optionComponents.get(optionId).checkbox).isSelected();
    }

    public String getOptionText(String optionId){
        String text = null;
        JTextField textField = (JTextField) optionComponents.get(optionId).textfield;
        if (textField != null){
            text = textField.getText();
        }
        
        return text;
    }

    public void setOptionActive(String optionId, boolean selected){
        ((JCheckBox) optionComponents.get(optionId).checkbox).setSelected(selected);
    }

    public void setOptionText(String optionId, String text){
        ((JTextField) optionComponents.get(optionId).textfield).setText(text);
    }

}
