package gitflow.actions;

import com.intellij.dvcs.ui.BranchActionGroup;
import com.intellij.dvcs.ui.PopupElementWithAdditionalInfo;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;

class RepoActions extends BranchActionGroup implements PopupElementWithAdditionalInfo, FileEditorManagerListener {
    Project myProject;
    GitRepository myRepo;
    private final ArrayList<AnAction> repoActions;

    RepoActions(@NotNull Project project, @NotNull GitRepository repo) {
        myProject = project;
        myRepo = repo;

        String repoName = repo.getRoot().getPresentableName();
        getTemplatePresentation().setText(repoName, false); // no mnemonics
        this.updateFavoriteIcon();
        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);

        repoActions = getRepoActions();
    }

    public ArrayList<AnAction> getRepoActions(){
        ArrayList<AnAction> actionList = new ArrayList<AnAction>();

        actionList.add(new InitRepoAction(myRepo));

        //FEATURE ACTIONS
        actionList.add(new Separator("Feature"));
        actionList.add(new StartFeatureAction(myRepo));
        actionList.add(new FinishFeatureAction(myRepo));
        actionList.add(new PublishFeatureAction(myRepo));
        actionList.add(new TrackFeatureAction(myRepo));

        //RELEASE ACTIONS
        actionList.add(new Separator("Release"));
        actionList.add(new StartReleaseAction(myRepo));
        actionList.add(new FinishReleaseAction(myRepo));
        actionList.add(new PublishReleaseAction(myRepo));
        actionList.add(new TrackReleaseAction(myRepo));

        //BUGFIX ACTIONS
        actionList.add(new Separator("Bugfix"));
        actionList.add(new StartBugfixAction(myRepo));
        actionList.add(new FinishBugfixAction(myRepo));
        actionList.add(new PublishBugfixAction(myRepo));
        actionList.add(new TrackBugfixAction(myRepo));

        //HOTFIX ACTIONS
        actionList.add(new Separator("Hotfix"));
        actionList.add(new StartHotfixAction(myRepo));
        actionList.add(new FinishHotfixAction(myRepo));
        actionList.add(new PublishHotfixAction(myRepo));

        return actionList;
    }

    public DefaultActionGroup getRepoActionGroup(){
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        Iterator actionsIterator = this.getRepoActions().iterator();
        while(actionsIterator.hasNext()){
            AnAction action = (AnAction) actionsIterator.next();
            actionGroup.add(action);
        }

        return actionGroup;
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent e) {
        ArrayList<AnAction> children = new ArrayList<AnAction>(this.repoActions);

        return children.toArray(new AnAction[0]);
    }

    @Override
    @Nullable
    public String getInfoText() {
        return "what's this";
    }

    public void updateFavoriteIcon(){
        boolean isFavorite = GitBranchUtil.getCurrentRepository(myProject) == myRepo;
        setFavorite(isFavorite);
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        this.updateFavoriteIcon();
    }
}

