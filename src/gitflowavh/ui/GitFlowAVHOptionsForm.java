package gitflowavh.ui;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Andreas Vogler (Andreas.Vogler@geneon.de)
 * @author Opher Vishnia (opherv@gmail.com)
 */
public class GitFlowAVHOptionsForm implements ItemListener {
    private JPanel contentPane;
    private JCheckBox releaseFetchOrigin;
    private JCheckBox featureKeepRemote;
    private JCheckBox featureFetchOrigin;

    private JCheckBox pushOnFinishRelease;
    private JCheckBox dontTagRelease;
    private JCheckBox useCustomTagCommitMessage;
    private JTextField customTagCommitMessage;

    private JCheckBox hotfixFetchOrigin;
    private JCheckBox pushOnFinishHotfix;
    private JCheckBox dontTagHotfix;
    private JCheckBox useCustomHotfixCommitMessage;
    private JTextField customHotfixCommitMessage;

    private JCheckBox bugfixKeepRemote;
    private JCheckBox bugfixFetchOrigin;

    public JPanel getContentPane() {
        dontTagRelease.addItemListener(this);
        dontTagHotfix.addItemListener(this);
        useCustomTagCommitMessage.addItemListener(this);
        useCustomHotfixCommitMessage.addItemListener(this);
        return contentPane;
    }

    /**
     * Listens to the check boxes.
     */
    public void itemStateChanged(ItemEvent e) {

        Object source = e.getItemSelectable();

        // Disable\enable the finish release tag commit message according to the checkbox state
        if (source == useCustomTagCommitMessage) {
            if (e.getStateChange() == ItemEvent.SELECTED && !dontTagRelease.isSelected()) {
                customTagCommitMessage.setEditable(true);
                customTagCommitMessage.setEnabled(true);
            } else {
                customTagCommitMessage.setEditable(false);
            }
        } else if (source == dontTagRelease) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                useCustomTagCommitMessage.setEnabled(false);
                customTagCommitMessage.setEnabled(false);
            } else {
                useCustomTagCommitMessage.setEnabled(true);
                if (useCustomTagCommitMessage.isSelected()) {
                    customTagCommitMessage.setEnabled(true);
                    customTagCommitMessage.setEditable(true);
                }
            }
        }

        // Disable\enable the finish hotfix tag commit message according to the checkbox state
        if (source == useCustomHotfixCommitMessage) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                customHotfixCommitMessage.setEditable(true);
                customHotfixCommitMessage.setEnabled(true);
            } else {
                customHotfixCommitMessage.setEditable(false);
            }
        } else if (source == dontTagHotfix) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                useCustomHotfixCommitMessage.setEnabled(false);
                customHotfixCommitMessage.setEnabled(false);
            } else {
                useCustomHotfixCommitMessage.setEnabled(true);
                if (useCustomHotfixCommitMessage.isSelected()) {
                    customHotfixCommitMessage.setEnabled(true);
                    customHotfixCommitMessage.setEditable(true);
                }
            }
        }


    }

    // Feature getters/setters

    /**
     * @return boolean
     */
    public boolean isFeatureFetchOrigin() {
        return featureFetchOrigin.isSelected();
    }

    /**
     * @param selected boolean
     */
    public void setFeatureFetchOrigin(boolean selected) {
        featureFetchOrigin.setSelected(selected);
    }

    /**
     * @return boolean
     */
    public boolean isFeatureKeepRemote() {
        return featureKeepRemote.isSelected();
    }

    /**
     * @param selected boolean
     */
    public void setFeatureKeepRemote(boolean selected) {
        featureKeepRemote.setSelected(selected);
    }

    // Release getters/setters

    /**
     * @return boolean
     */
    public boolean isReleaseFetchOrigin() {
        return releaseFetchOrigin.isSelected();
    }

    /**
     * @param selected boolean
     */
    public void setReleaseFetchOrigin(boolean selected) {
        releaseFetchOrigin.setSelected(selected);
    }

    /**
     * @param selected boolean
     */
    public void setDontTagRelease(boolean selected) {
        dontTagRelease.setSelected(selected);
    }

    /**
     * @return boolean
     */
    public boolean isPushOnFinishRelease() {
        return pushOnFinishRelease.isSelected();
    }

    /**
     * @param selected boolean
     */
    public void setPushOnFinishRelease(boolean selected) {
        pushOnFinishRelease.setSelected(selected);
    }

    /**
     * @return boolean
     */
    public boolean isDontTagRelease() {
        return dontTagRelease.isSelected();
    }

    /* Custom finish release tag commit message */

    /**
     * @return boolean
     */
    public boolean isUseCustomTagCommitMessage() {
        return useCustomTagCommitMessage.isSelected();
    }

    /**
     * @param selected boolean
     */
    public void setUseCustomTagCommitMessage(boolean selected) {
        useCustomTagCommitMessage.setSelected(selected);
    }

    /**
     * @return String
     */
    public String getCustomTagCommitMessage() {
        return customTagCommitMessage.getText();
    }

    /**
     * @param message String
     */
    public void setCustomTagCommitMessage(String message) {
        customTagCommitMessage.setText(message);
    }

    // Hotfix getters/setters

    /**
     * @return boolean
     */
    public boolean isHotfixFetchOrigin() {
        return hotfixFetchOrigin.isSelected();
    }

    /**
     * @param selected boolean
     */
    public void setHotfixFetchOrigin(boolean selected) {
        hotfixFetchOrigin.setSelected(selected);
    }

    /**
     * @return boolean
     */
    public boolean isPushOnFinishHotfix() {
        return pushOnFinishHotfix.isSelected();
    }

    /**
     * @param selected boolean
     */
    public void setPushOnFinishHotfix(boolean selected) {
        pushOnFinishHotfix.setSelected(selected);
    }

    /**
     * @return boolean
     */
    public boolean isDontTagHotfix() {
        return dontTagHotfix.isSelected();
    }

    /**
     * @param selected boolean
     */
    public void setDontTagHotfix(boolean selected) {
        dontTagHotfix.setSelected(selected);
    }

    /* Custom finish hotfix commit message */

    /**
     * @return boolean
     */
    public boolean isUseCustomHotfixComitMessage() {
        return useCustomHotfixCommitMessage.isSelected();
    }

    /**
     * @param selected boolean
     */
    public void setUseCustomHotfixCommitMessage(boolean selected) {
        useCustomHotfixCommitMessage.setSelected(selected);
    }

    /**
     * @return String
     */
    public String getCustomHotfixCommitMessage() {
        return customHotfixCommitMessage.getText();
    }

    /**
     * @param message String
     */
    public void setCustomHotfixCommitMessage(String message) {
        customHotfixCommitMessage.setText(message);
    }

    // Bugfix getters/setters

    /**
     * @return boolean
     */
    public boolean isBugfixFetchOrigin() {
        return bugfixFetchOrigin.isSelected();
    }

    /**
     * @param selected boolean
     */
    public void setBugfixFetchOrigin(boolean selected) {
        bugfixFetchOrigin.setSelected(selected);
    }

    /**
     * @return boolean
     */
    public boolean isBugfixKeepRemote() {
        return bugfixKeepRemote.isSelected();
    }

    /**
     * @param selected boolean
     */
    public void setBugfixKeepRemote(boolean selected) {
        bugfixKeepRemote.setSelected(selected);
    }
}
