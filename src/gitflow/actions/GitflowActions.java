package gitflow.actions;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import gitflow.Gitflow;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowBranchUtilManager;
import gitflow.GitflowConfigUtil;
import gitflow.PopupElementWithAdditionalInfo;
import gitflow.BranchActionGroup;
import org.jetbrains.annotations.NotNull;

/**
 * All actions associated with Gitflow
 *
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */
public class GitflowActions {

    public static void runMergeTool(){
        git4idea.actions.GitResolveConflictsAction resolveAction= new git4idea.actions.GitResolveConflictsAction();
        AnActionEvent e = new AnActionEvent(null, DataManager.getInstance().getDataContext(), ActionPlaces.UNKNOWN, new Presentation(""), ActionManager.getInstance(), 0);
        resolveAction.actionPerformed(e);
    }
}
