package gitflow;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.commands.GitCommand;
import git4idea.commands.GitLineHandler;
import git4idea.commands.GitLineHandlerListener;
import git4idea.util.GitVcsConsoleWriter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class GitInitLineHandler extends GitLineHandler {
    private final GitVcsConsoleWriter consoleWriter;

    private BufferedWriter writer;
    GitflowInitOptions _initOptions;

    public GitInitLineHandler(GitflowInitOptions initOptions,
            @NotNull Project project, @NotNull VirtualFile vcsRoot,
            @NotNull GitCommand command) {
        super(project, vcsRoot, command);
        consoleWriter = GitVcsConsoleWriter.getInstance(project);
        _initOptions = initOptions;

        // `GitHandler.onTextAvailable` was removed from the platform, so the
        // interactive answers are provided through a line listener instead.
        addLineListener(new GitLineHandlerListener() {
            @Override
            public void onLineAvailable(@NotNull String line, @NotNull Key outputType) {
                answerPrompt(line);
            }
        });
    }

    @Override
    protected Process startProcess() throws ExecutionException {
        Process p = super.startProcess();
        writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        return p;
    }

    private void answerPrompt(@NotNull String s) {
        try {
            if (s.contains("name for production releases")) {
                consoleWriter.showCommandLine(_initOptions.getProductionBranch());

                writer.write(_initOptions.getProductionBranch());
                writer.write("\n");
                writer.flush();
            }

            if (s.contains("name for \"next release\"")) {
                consoleWriter.showCommandLine(_initOptions.getDevelopmentBranch());

                writer.write(_initOptions.getDevelopmentBranch());
                writer.write("\n");
                writer.flush();
            }

            if (s.contains("Feature branches")) {
                consoleWriter.showCommandLine(_initOptions.getFeaturePrefix());

                writer.write(_initOptions.getFeaturePrefix());
                writer.write("\n");
                writer.flush();
            }
            if (s.contains("Bugfix branches")) {
                consoleWriter.showCommandLine(_initOptions.getBugfixPrefix());

                writer.write(_initOptions.getBugfixPrefix());
                writer.write("\n");
                writer.flush();
            }
            if (s.contains("Release branches")) {
                consoleWriter.showCommandLine(_initOptions.getReleasePrefix());

                writer.write(_initOptions.getReleasePrefix());
                writer.write("\n");
                writer.flush();
            }
            if (s.contains("Hotfix branches")) {
                consoleWriter.showCommandLine(_initOptions.getHotfixPrefix());

                writer.write(_initOptions.getHotfixPrefix());
                writer.write("\n");
                writer.flush();
            }
            if (s.contains("Support branches")) {
                consoleWriter.showCommandLine(_initOptions.getSupportPrefix());

                writer.write(_initOptions.getSupportPrefix());
                writer.write("\n");
                writer.flush();
            }
            if (s.contains("Version tag")) {
                consoleWriter.showCommandLine(_initOptions.getVersionPrefix());

                writer.write(_initOptions.getVersionPrefix());
                writer.write("\n");
                writer.flush();
            }
            if (s.contains("Hooks and filters")) {
                writer.write("\n");
                writer.flush();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
