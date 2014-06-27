package gitflow.ui;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Andreas Vogler (Andreas.Vogler@geneon.de)
 * @author Opher Vishnia (opherv@gmail.com)
 */
public class GitflowOptionsForm  implements ItemListener {
    private JCheckBox pushOnFinishRelease;
    private JPanel contentPane;
    private JCheckBox pushOnFinishHotfix;
    private JCheckBox dontTagRelease;
    private JCheckBox useCustomTagCommitMessage;
    private JTextField customTagCommitMessage;
    private JTextField customHotfixCommitMessage;
    private JCheckBox useCustomHotfixCommitMessage;
    private JCheckBox dontTagHotfix;

    public JPanel getContentPane() {
        dontTagRelease.addItemListener(this);
        dontTagHotfix.addItemListener(this);
        useCustomTagCommitMessage.addItemListener(this);
        useCustomHotfixCommitMessage.addItemListener(this);
        return contentPane;
    }

    /** Listens to the check boxes. */
    public void itemStateChanged(ItemEvent e) {

        Object source = e.getItemSelectable();

        //disable\enable the finish release tag commit message according to the checkbox state
        if (source == useCustomTagCommitMessage) {
            if (e.getStateChange() == ItemEvent.SELECTED && dontTagRelease.isSelected()==false) {
                customTagCommitMessage.setEditable(true);
                customTagCommitMessage.setEnabled(true);
            }
            else{
                customTagCommitMessage.setEditable(false);
            }
        }
        else if (source == dontTagRelease) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                useCustomTagCommitMessage.setEnabled(false);
                customTagCommitMessage.setEnabled(false);
            }
            else{
                useCustomTagCommitMessage.setEnabled(true);
                if( useCustomTagCommitMessage.isSelected()){
                    customTagCommitMessage.setEnabled(true);
                    customTagCommitMessage.setEditable(true);
                }
            }
        }

        //disable\enable the finish hotfix tag commit message according to the checkbox state
        if (source == useCustomHotfixCommitMessage) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                customHotfixCommitMessage.setEditable(true);
                customHotfixCommitMessage.setEnabled(true);
            }
            else{
                customHotfixCommitMessage.setEditable(false);
            }
        }
        else if (source == dontTagHotfix) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                useCustomHotfixCommitMessage.setEnabled(false);
                customHotfixCommitMessage.setEnabled(false);
            }
            else{
                useCustomHotfixCommitMessage.setEnabled(true);
                if( useCustomHotfixCommitMessage.isSelected()){
                    customHotfixCommitMessage.setEnabled(true);
                    customHotfixCommitMessage.setEditable(true);
                }
            }
        }


    }

    public boolean isPushOnFinishRelease()
    {
        return pushOnFinishRelease.isSelected();
    }

    public void setPushOnFinishRelease(boolean selected)
    {
        pushOnFinishRelease.setSelected(selected);
    }

    public boolean isPushOnFinishHotfix() {
        return pushOnFinishHotfix.isSelected();
    }

    public void setPushOnFinishHotfix(boolean selected) {
        pushOnFinishHotfix.setSelected(selected);
    }

    public boolean isDontTagRelease() { return dontTagRelease.isSelected(); }

    public boolean isDontTagHotfix() { return dontTagHotfix.isSelected(); }

    public void setDontTagRelease(boolean selected) {
        dontTagRelease.setSelected(selected);
    }

    public void setDontTagHotfix(boolean selected) { dontTagHotfix.setSelected(selected); }


    /* custom finish release tag commit message */

    public boolean isUseCustomTagCommitMessage() {
        return useCustomTagCommitMessage.isSelected();
    }

    public void setUseCustomTagCommitMessage(boolean selected) {
        useCustomTagCommitMessage.setSelected(selected);
    }

    public String getCustomTagCommitMessage() {
        return customTagCommitMessage.getText();
    }

    public void setCustomTagCommitMessage(String message) {
        customTagCommitMessage.setText(message);
    }

    /* custom finish hotfix commit message */

    public boolean isUseCustomHotfixComitMessage(){
        return useCustomHotfixCommitMessage.isSelected();
    }

    public void setUseCustomHotfixCommitMessage(boolean selected){
        useCustomHotfixCommitMessage.setSelected(selected);
    }

    public String getCustomHotfixCommitMessage(){
        return customHotfixCommitMessage.getText();
    }

    public void setCustomHotfixCommitMessage(String message){
        customHotfixCommitMessage.setText(message);
    }
}
