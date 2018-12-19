package gitflow;

import git4idea.commands.Git;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitLineHandlerListener;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */
public interface Gitflow extends Git {

    public GitCommandResult initRepo(@NotNull GitRepository repository,
                                     GitflowInitOptions initOptions, @Nullable GitLineHandlerListener... listeners);


    // feature

    GitCommandResult startFeature(@NotNull GitRepository repository,
                                  @NotNull String featureName,
                                  @Nullable String baseBranch,
                                  @Nullable GitLineHandlerListener... listeners);

    GitCommandResult finishFeature(@NotNull GitRepository repository,
                                   @NotNull String featureName,
                                   @Nullable GitLineHandlerListener... listeners);

    GitCommandResult publishFeature(@NotNull GitRepository repository,
                                    @NotNull String featureName,
                                    @Nullable GitLineHandlerListener... listeners);

    GitCommandResult pullFeature(@NotNull GitRepository repository,
                                 @NotNull String featureName,
                                 @NotNull GitRemote remote,
                                 @Nullable GitLineHandlerListener... listeners);

    GitCommandResult trackFeature(@NotNull GitRepository repository,
                                  @NotNull String featureName,
                                  @NotNull GitRemote remote,
                                  @Nullable GitLineHandlerListener... listeners);

    //release

    GitCommandResult startRelease(@NotNull GitRepository repository,
                                  @NotNull String releaseName,
                                  @Nullable GitLineHandlerListener... listeners);


    GitCommandResult finishRelease(@NotNull GitRepository repository,
                                   @NotNull String releaseName,
                                   @NotNull String tagMessage,
                                   @Nullable GitLineHandlerListener... listeners);


    GitCommandResult publishRelease(@NotNull GitRepository repository,
                                    @NotNull String releaseName,
                                    @Nullable GitLineHandlerListener... listeners);

    GitCommandResult trackRelease(@NotNull GitRepository repository,
                                  @NotNull String releaseName,
                                  @Nullable GitLineHandlerListener... listeners);

    //hotfix

    GitCommandResult startHotfix(@NotNull GitRepository repository,
                                 @NotNull String hotfixName,
                                 @Nullable String baseBranch,
                                 @Nullable GitLineHandlerListener... listeners);

    GitCommandResult finishHotfix(@NotNull GitRepository repository,
                                  @NotNull String hotfixName,
                                  @NotNull String tagMessage,
                                  @Nullable GitLineHandlerListener... listeners);

    GitCommandResult publishHotfix(@NotNull GitRepository repository,
                                   @NotNull String hotfixName,
                                   @Nullable GitLineHandlerListener... listeners);

    // Bugfix

    GitCommandResult startBugfix(@NotNull GitRepository repository,
                                  @NotNull String bugfixName,
                                  @Nullable String baseBranch,
                                  @Nullable GitLineHandlerListener... listeners);

    GitCommandResult finishBugfix(@NotNull GitRepository repository,
                                   @NotNull String bugfixName,
                                   @Nullable GitLineHandlerListener... listeners);

    GitCommandResult publishBugfix(@NotNull GitRepository repository,
                                    @NotNull String bugfixName,
                                    @Nullable GitLineHandlerListener... listeners);

    GitCommandResult pullBugfix(@NotNull GitRepository repository,
                                 @NotNull String bugfixName,
                                 @NotNull GitRemote remote,
                                 @Nullable GitLineHandlerListener... listeners);

    GitCommandResult trackBugfix(@NotNull GitRepository repository,
                                  @NotNull String bugfixName,
                                  @NotNull GitRemote remote,
                                  @Nullable GitLineHandlerListener... listeners);

}
