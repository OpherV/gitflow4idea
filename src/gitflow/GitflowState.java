package gitflow;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.tasks.Task;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Persistent state for Gitflow component
 * Created by opherv on 8/17/16.
 */

@State(
        name = "GitflowState", storages = {
        @Storage(
                id = "other",
                file = "$APP_CONFIG$/GitflowState.xml")
})
public class GitflowState implements PersistentStateComponent<GitflowState> {

    private HashMap<String, String> taskBranches;


    public GitflowState() {
        taskBranches = new HashMap<String, String>();
    }


    public HashMap<String, String> getTaskBranches() {
        return taskBranches;
    }

    public void setTaskBranches(HashMap<String, String> taskBranches) {
        this.taskBranches = taskBranches;
    }

    @Nullable
    @Override
    public GitflowState getState() {
        return this;
    }

    @Override
    public void loadState(GitflowState state) {
        XmlSerializerUtil.copyBean(state, this);

    }

    public String getTaskBranch(Task task){
        return taskBranches.get(task.getId());
    }


    public void setTaskBranch(Task task, String branchName){
        taskBranches.put(task.getId(), branchName);
    }
}
