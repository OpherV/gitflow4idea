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


    // feature

    GitCommandResult startFeature(@NotNull GitRepository repository,
                                  @NotNull String featureName,
                                  @Nullable GitLineHandlerListener... listeners);

    GitCommandResult finishFeature(@NotNull GitRepository repository,
                                  @NotNull String featureName,
                                  @Nullable GitLineHandlerListener... listeners);

    GitCommandResult publishFeature(@NotNull GitRepository repository,
                                   @NotNull String featureName,
                                   @Nullable GitLineHandlerListener... listeners);

    GitCommandResult pullFeature(@NotNull GitRepository repository,
                                    @NotNull String featureName,
                                    @NotNull String remoteName,
                                    @Nullable GitLineHandlerListener... listeners);


    //release

    GitCommandResult startRelease(@NotNull GitRepository repository,
                                  @NotNull String releaseName,
                                  @Nullable GitLineHandlerListener... listeners);


    GitCommandResult finishRelease(@NotNull GitRepository repository,
                                   @NotNull String releaseName,
                                   @Nullable GitLineHandlerListener... listeners);


    GitCommandResult publishRelease(@NotNull GitRepository repository,
                                    @NotNull String releaseName,
                                    @Nullable GitLineHandlerListener... listeners);

    GitCommandResult trackRelease(@NotNull GitRepository repository,
                                 @NotNull String releaseName,
                                 @Nullable GitLineHandlerListener... listeners);

}
