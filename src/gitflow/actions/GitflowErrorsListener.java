package gitflow.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import gitflow.ui.NotifyUtil;

public class GitflowErrorsListener extends GitflowLineHandler{

    boolean hasMergeError=false;

    GitflowErrorsListener(Project project){
        myProject=project;
    }

    @Override
    public void onLineAvailable(String line, Key outputType) {
        if (line.contains("'flow' is not a git command")) {
            NotifyUtil.notifyError(myProject, "Error", "Gitflow is not installed");
        }
        if (line.contains("Not a gitflow-enabled repo yet")) {
            NotifyUtil.notifyError(myProject, "Error", "Not a gitflow-enabled repo yet. Please init git flow");
        }
        if (line.contains("There were merge conflicts")){
            hasMergeError=true;
        }
    }

};