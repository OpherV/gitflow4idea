package git4idea.gitflow;

import git4idea.commands.Git;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitLineHandlerListener;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Opher
 * Date: 8/18/13
 * Time: 5:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Gitflow extends Git {

    public GitCommandResult initRepo(@NotNull GitRepository repository,
                                     @Nullable GitLineHandlerListener... listeners);

    GitCommandResult startFeature(@NotNull GitRepository repository,
                                  @NotNull String featureName,
                                  @Nullable GitLineHandlerListener... listeners);

    GitCommandResult finishFeature(@NotNull GitRepository repository,
                                  @NotNull String featureName,
                                  @Nullable GitLineHandlerListener... listeners);


}
