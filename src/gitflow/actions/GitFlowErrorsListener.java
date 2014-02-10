package gitflow.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import git4idea.util.GitUIUtil;

public class GitflowErrorsListener extends GitflowLineHandler{

    boolean hasMergeError=false;

    GitflowErrorsListener(Project project){
        myProject=project;
    }

    @Override
    public void onLineAvailable(String line, Key outputType) {
        if (line.contains("'flow' is not a git command")){
            GitUIUtil.notifyError(myProject, "Error", "Gitflow is not installed");
        }
        if (line.contains("Not a gitflow-enabled repo yet")){
            GitUIUtil.notifyError(myProject,"Error","Not a gitflow-enabled repo yet. Please init git flow");
        }
        if (line.contains("There were merge conflicts")){
            hasMergeError=true;
        }
    }

};