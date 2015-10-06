package gitflowavh.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import gitflowavh.ui.NotifyUtil;

public class GitFlowAVHErrorsListener extends GitFlowAVHLineHandler {

    boolean hasMergeError=false;

    /**
     * @param project Project
     */
    GitFlowAVHErrorsListener(Project project){
        myProject=project;
    }

    /**
     * @param line String
     * @param outputType Key
     */
    @Override
    public void onLineAvailable(String line, Key outputType) {
        if (line.contains("'flow' is not a git command")) {
            NotifyUtil.notifyError(myProject, "Error", "GitFlowAVH is not installed");
        }
        if (line.contains("Not a gitflow-enabled repo yet")) {
            NotifyUtil.notifyError(myProject, "Error", "Not a gitflow-enabled repo yet. Please init git flow");
        }
        if (line.contains("There were merge conflicts")){
            hasMergeError=true;
        }
    }
}