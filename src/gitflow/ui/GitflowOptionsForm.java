package gitflow.ui;

import javax.swing.*;

/**
 * @author Andreas Vogler (Andreas.Vogler@geneon.de)
 */
public class GitflowOptionsForm {
    private JCheckBox pushOnFinishRelease;
    private JPanel contentPane;

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
}
