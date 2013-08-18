package git4idea.commands;

import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Opher
 * Date: 8/18/13
 * Time: 6:15 PM
 */
public class GitflowImpl extends GitImpl {

    public GitCommandResult startFeature(@NotNull GitRepository repository,
                                         @NotNull String featureName,
                                         @NotNull GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.FLOW);
        h.setSilent(false);

        h.addParameters("feature");
        h.addParameters("start");
        h.addParameters(featureName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }
}
