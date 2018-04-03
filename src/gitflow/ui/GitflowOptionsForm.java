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


        HashMap<GitflowOptionsFactory.TYPE, JPanel> branchTypeToPanel = new HashMap<GitflowOptionsFactory.TYPE, JPanel>();

        branchTypeToPanel.put(GitflowOptionsFactory.TYPE.FEATURE, featurePanel);
        branchTypeToPanel.put(GitflowOptionsFactory.TYPE.RELEASE, releasePanel);
        branchTypeToPanel.put(GitflowOptionsFactory.TYPE.HOTFIX, hotfixPanel);

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
                optionRow.add(checkbox);

                JTextField textField = null;

                // some options have input text
                if (optionMap.get("inputText") != null) {
                    textField = new JTextField();
                    textField.setText(optionMap.get("inputText"));
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
//        dontTagRelease.addItemListener(this);
//        dontTagHotfix.addItemListener(this);
//        useCustomTagCommitMessage.addItemListener(this);
//        useCustomHotfixCommitMessage.addItemListener(this);

        return contentPane;
    }

    /** Listens to the check boxes. */
    public void itemStateChanged(ItemEvent e) {

        Object source = e.getItemSelectable();

//        //disable\enable the finish release tag commit message according to the checkbox state
//        if (source == useCustomTagCommitMessage) {
//            if (e.getStateChange() == ItemEvent.SELECTED && dontTagRelease.isSelected()==false) {
//                customTagCommitMessage.setEditable(true);
//                customTagCommitMessage.setEnabled(true);
//            }
//            else{
//                customTagCommitMessage.setEditable(false);
//            }
//        }
//        else if (source == dontTagRelease) {
//            if (e.getStateChange() == ItemEvent.SELECTED) {
//                useCustomTagCommitMessage.setEnabled(false);
//                customTagCommitMessage.setEnabled(false);
//            }
//            else{
//                useCustomTagCommitMessage.setEnabled(true);
//                if( useCustomTagCommitMessage.isSelected()){
//                    customTagCommitMessage.setEnabled(true);
//                    customTagCommitMessage.setEditable(true);
//                }
//            }
//        }
//
//        //disable\enable the finish hotfix tag commit message according to the checkbox state
//        if (source == useCustomHotfixCommitMessage) {
//            if (e.getStateChange() == ItemEvent.SELECTED) {
//                customHotfixCommitMessage.setEditable(true);
//                customHotfixCommitMessage.setEnabled(true);
//            }
//            else{
//                customHotfixCommitMessage.setEditable(false);
//            }
//        }
//        else if (source == dontTagHotfix) {
//            if (e.getStateChange() == ItemEvent.SELECTED) {
//                useCustomHotfixCommitMessage.setEnabled(false);
//                customHotfixCommitMessage.setEnabled(false);
//            }
//            else{
//                useCustomHotfixCommitMessage.setEnabled(true);
//                if( useCustomHotfixCommitMessage.isSelected()){
//                    customHotfixCommitMessage.setEnabled(true);
//                    customHotfixCommitMessage.setEditable(true);
//                }
//            }
//        }
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
