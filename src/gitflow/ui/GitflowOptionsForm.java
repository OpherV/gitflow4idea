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

    public JPanel getContentPane() {
        return contentPane;
    }

    /** Listens to the check boxes. */
    public void itemStateChanged(ItemEvent e) {

        Object source = e.getItemSelectable();

        //disable\enable the tag commit message according to the checkbox state
        if (source == useCustomTagCommitMessage) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                customTagCommitMessage.setEnabled(true);
            }
            else{
                customTagCommitMessage.setEnabled(false);
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

    public boolean isDontTagRelease() {
        return dontTagRelease.isSelected();
    }

    public void setDontTagRelease(boolean selected) {
        dontTagRelease.setSelected(selected);
    }


    /* custom release tag commit message */

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
}
