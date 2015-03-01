package gitflow;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.commands.GitCommand;
import git4idea.commands.GitLineHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class GitInitLineHandler extends GitLineHandler {
    private BufferedWriter writer;
    GitflowInitOptions _initOptions;

    public GitInitLineHandler(GitflowInitOptions initOptions,@NotNull Project project, @NotNull VirtualFile vcsRoot, @NotNull GitCommand command) {
        super(project, vcsRoot, command);
        _initOptions=initOptions;
    }

    @Nullable
    @Override
    protected Process startProcess() throws ExecutionException {
        Process p = super.startProcess();
        writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        return p;
    }

    protected void processTerminated(final int exitCode) {
        super.processTerminated(exitCode);
    }


    @Override
    protected void onTextAvailable(String s, Key key) {
        super.onTextAvailable(s,key);
        try {
            if (s.contains("Branch name for production releases")){
                writer.write(_initOptions.getProductionBranch());
                myVcs.showCommandLine(_initOptions.getProductionBranch());
                writer.newLine();
                writer.flush();
            }

            if (s.contains("Branch name for \"next release\"") ||
                s.contains("Which branch should be used for integration of the")){

                writer.write(_initOptions.getDevelopmentBranch());
                myVcs.showCommandLine(_initOptions.getDevelopmentBranch());
                writer.newLine();
                writer.flush();
            }
            if (s.contains("Feature branches")){
                writer.write(_initOptions.getFeaturePrefix());
                myVcs.showCommandLine(_initOptions.getFeaturePrefix());
                writer.newLine();
                writer.flush();
            }
            if (s.contains("Release branches")){
                writer.write(_initOptions.getReleasePrefix());
                myVcs.showCommandLine(_initOptions.getReleasePrefix());
                writer.newLine();
                writer.flush();
            }
            if (s.contains("Hotfix branches")){
                writer.write(_initOptions.getHotfixPrefix());
                myVcs.showCommandLine(_initOptions.getHotfixPrefix());
                writer.newLine();
                writer.flush();
            }
            if (s.contains("Support branches")){
                writer.write(_initOptions.getSupportPrefix());
                myVcs.showCommandLine(_initOptions.getSupportPrefix());
                writer.newLine();
                writer.flush();
            }
            if (s.contains("Version tag")){
                writer.write(_initOptions.getVersionPrefix());
                myVcs.showCommandLine(_initOptions.getVersionPrefix());
                writer.newLine();
                writer.flush();
            }
            if (s.contains("Hooks and filters")){
                writer.newLine();
                writer.flush();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
