package gitflow.ui;

import javax.swing.*;

/**
 * @author Andreas Vogler (Andreas.Vogler@geneon.de)
 */
public class GitflowOptionsForm {
    private JCheckBox pushOnFinishRelease;
    private JPanel contentPane;
    private JCheckBox pushOnFinishHotfix;

    public JPanel getContentPane() {
        return contentPane;
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
}
