package gitflow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;

import java.awt.*;

public class IDEAUtils {
    public static Project getActiveProject(){
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        Project activeProject = null;
        for (Project project : projects) {
            Window window = WindowManager.getInstance().suggestParentWindow(project);
            if (window != null && window.isActive()) {
                activeProject = project;
            }
        }
        return activeProject;
    }
}
