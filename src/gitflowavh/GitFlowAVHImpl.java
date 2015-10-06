package gitflowavh;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import git4idea.commands.GitCommand;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitImpl;
import git4idea.commands.GitLineHandler;
import git4idea.commands.GitLineHandlerListener;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;


public class GitFlowAVHImpl extends GitImpl implements GitFlowAVH {

    /**
     * We must use reflection to add this command, since the git4idea implementation doesn't expose it.
     *
     * @return GitCommand
     */
    private GitCommand GitflowCommand() {
        Method m = null;
        try {
            m = GitCommand.class.getDeclaredMethod("write", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        //m.invoke(d);//exception java.lang.IllegalAccessException
        assert m != null;
        m.setAccessible(true); // Abracadabra

        GitCommand command = null;

        try {
            command = (GitCommand) m.invoke(null, "flow"); // Now it's ok
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return command;
    }

    /**
     * We must use reflection to add this command, since the git4idea implementation doesn't expose it.
     *
     * @param handler GitLineHandler
     * @return GitCommandResult
     */
    private static GitCommandResult run(@org.jetbrains.annotations.NotNull GitLineHandler handler) {
        Method m = null;
        try {
            m = GitImpl.class.getDeclaredMethod("run", GitLineHandler.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        assert m != null;
        m.setAccessible(true); // Abracadabra

        GitCommandResult result = null;

        try {
            result = (GitCommandResult) m.invoke(null, handler); // Now it's ok
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * @param repository GitRepository
     * @param initOptions GitFlowAVHInitOptions
     * @param listeners GitLineHandlerListener
     * @return GitCommandResult
     */
    public GitCommandResult initRepo(@NotNull GitRepository repository,
                                     GitFlowAVHInitOptions initOptions, @Nullable GitLineHandlerListener... listeners) {

        GitCommandResult result;

        if (initOptions.isUseDefaults()) {
            final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
            h.setSilent(false);
            h.setStdoutSuppressed(false);
            h.setStderrSuppressed(false);

            h.addParameters("init");
            h.addParameters("-d");

            result = run(h);
        } else {

            final GitInitLineHandler h = new GitInitLineHandler(initOptions, repository.getProject(), repository.getRoot(), GitflowCommand());

            h.setSilent(false);
            h.setStdoutSuppressed(false);
            h.setStderrSuppressed(false);

            h.addParameters("init");

            assert listeners != null;
            for (GitLineHandlerListener listener : listeners) {
                h.addLineListener(listener);
            }
            result = run(h);
        }


        return result;
    }


    // Feature

    /**
     *
     * @param repository GitRepository
     * @param featureName String
     * @param baseBranch String
     * @param listeners GitLineHandlerListener
     * @return GitCommandResult
     */
    public GitCommandResult startFeature(@NotNull GitRepository repository,
                                         @NotNull String featureName,
                                         @Nullable String baseBranch,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        h.setSilent(false);

        h.addParameters("feature");
        h.addParameters("start");
        if (GitFlowAVHConfigurable.featureFetchOrigin(repository.getProject())) {
            h.addParameters("-F");
        }
        h.addParameters(featureName);

        if (baseBranch != null) {
            h.addParameters(baseBranch);
        }

        assert listeners != null;
        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    /**
     *
     * @param repository GitRepository
     * @param featureName String
     * @param listeners GitLineHandlerListener
     * @return GitCommandResult
     */
    public GitCommandResult finishFeature(@NotNull GitRepository repository,
                                          @NotNull String featureName,
                                          @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());

        setUrl(h, repository);
        h.setSilent(false);

        h.addParameters("feature");
        h.addParameters("finish");

        if (GitFlowAVHConfigurable.featureKeepRemote(repository.getProject())) {
            h.addParameters("--keepremote");
        }

        if (GitFlowAVHConfigurable.featureFetchOrigin(repository.getProject())) {
            h.addParameters("-F");
        }

        h.addParameters(featureName);

        assert listeners != null;
        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    /**
     * @param repository GitRepository
     * @param featureName String
     * @param listeners GitLineHandlerListener
     * @return GitCommandResult
     */
    public GitCommandResult publishFeature(@NotNull GitRepository repository,
                                           @NotNull String featureName,
                                           @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);

        h.addParameters("feature");
        h.addParameters("publish");
        h.addParameters(featureName);

        assert listeners != null;
        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    /**
     * Feature pull seems to be kind of useless. See:
     * http://stackoverflow.com/questions/18412750/why-doesnt-git-flow-feature-pull-track
     *
     * @param repository GitRepository
     * @param featureName String
     * @param remote GitRemote
     * @param listeners GitLineHandlerListener
     * @return GitCommandResult
     */
    public GitCommandResult pullFeature(@NotNull GitRepository repository,
                                        @NotNull String featureName,
                                        @NotNull GitRemote remote,
                                        @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);
        h.addParameters("feature");
        h.addParameters("pull");
        h.addParameters(remote.getName());
        h.addParameters(featureName);

        assert listeners != null;
        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    /**
     *
     * @param repository GitRepository
     * @param featureName String
     * @param remote GitRemote
     * @param listeners GitLineHandlerListener
     * @return GitCommandResult
     */
    public GitCommandResult trackFeature(@NotNull GitRepository repository,
                                         @NotNull String featureName,
                                         @NotNull GitRemote remote,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);
        h.addParameters("feature");
        h.addParameters("track");
        h.addParameters(featureName);

        assert listeners != null;
        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }


    // Release

    /**
     * @param repository GitRepository
     * @param releaseName String
     * @param listeners GitLineHandlerListener
     * @return GitCommandResult
     */
    public GitCommandResult startRelease(@NotNull GitRepository repository,
                                         @NotNull String releaseName,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("start");

        if (GitFlowAVHConfigurable.releaseFetchOrigin(repository.getProject())) {
            h.addParameters("-F");
        }

        h.addParameters(releaseName);

        assert listeners != null;
        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    /**
     * @param repository GitRepository
     * @param releaseName String
     * @param tagMessage String
     * @param listeners GitLineHandlerListener
     * @return GitCommandResult
     */
    public GitCommandResult finishRelease(@NotNull GitRepository repository,
                                          @NotNull String releaseName,
                                          @NotNull String tagMessage,
                                          @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("finish");
        if (GitFlowAVHConfigurable.releaseFetchOrigin(repository.getProject())) {
            h.addParameters("-F");
        }
        if (GitFlowAVHConfigurable.pushOnReleaseFinish(repository.getProject())) {
            h.addParameters("-p");
        }

        if (GitFlowAVHConfigurable.dontTagRelease(repository.getProject())) {
            h.addParameters("-n");
        } else {
            h.addParameters("-m");
            h.addParameters(tagMessage);
        }

        h.addParameters(releaseName);

        assert listeners != null;
        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    /**
     * @param repository GitRepository
     * @param releaseName String
     * @param listeners GitLineHandlerListener
     * @return GitCommandResult
     */
    public GitCommandResult publishRelease(@NotNull GitRepository repository,
                                           @NotNull String releaseName,
                                           @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);

        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("publish");
        h.addParameters(releaseName);

        assert listeners != null;
        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    /**
     * @param repository GitRepository
     * @param releaseName String
     * @param listeners GitLineHandlerListener
     * @return GitCommandResult
     */
    public GitCommandResult trackRelease(@NotNull GitRepository repository,
                                         @NotNull String releaseName,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("track");
        h.addParameters(releaseName);

        assert listeners != null;
        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }


    // Hotfix

    /**
     * @param repository GitRepository
     * @param hotfixName String
     * @param baseBranch String
     * @param listeners GitLineHandlerListener
     * @return GitCommandResult
     */
    public GitCommandResult startHotfix(@NotNull GitRepository repository,
                                        @NotNull String hotfixName,
                                        @Nullable String baseBranch,
                                        @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        h.setSilent(false);

        h.addParameters("hotfix");
        h.addParameters("start");
        if (GitFlowAVHConfigurable.hotfixFetchOrigin(repository.getProject())) {
            h.addParameters("-F");
        }
        h.addParameters(hotfixName);

        if (baseBranch != null) {
            h.addParameters(baseBranch);
        }

        assert listeners != null;
        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    /**
     * @param repository GitRepository
     * @param hotfixName String
     * @param tagMessage String
     * @param listeners GitLineHandlerListener
     * @return GitCommandResult
     */
    public GitCommandResult finishHotfix(@NotNull GitRepository repository,
                                         @NotNull String hotfixName,
                                         @NotNull String tagMessage,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);

        h.addParameters("hotfix");
        h.addParameters("finish");
        if (GitFlowAVHConfigurable.hotfixFetchOrigin(repository.getProject())) {
            h.addParameters("-F");
        }
        if (GitFlowAVHConfigurable.pushOnHotfixFinish(repository.getProject())) {
            h.addParameters("-p");
        }


        if (GitFlowAVHConfigurable.dontTagHotfix(repository.getProject())) {
            h.addParameters("-n");
        } else {
            h.addParameters("-m");
            h.addParameters(tagMessage);
        }

        h.addParameters(hotfixName);

        assert listeners != null;
        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    /**
     * @param repository GitRepository
     * @param hotfixName String
     * @param listeners GitLineHandlerListener
     * @return GitCommandResult
     */
    public GitCommandResult publishHotfix(@NotNull GitRepository repository,
                                          @NotNull String hotfixName,
                                          @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);

        h.setSilent(false);

        h.addParameters("hotfix");
        h.addParameters("publish");
        h.addParameters(hotfixName);

        assert listeners != null;
        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    /**
     * @param h GitLineHandler
     * @param repository GitRepository
     */
    private void setUrl(GitLineHandler h, GitRepository repository) {
        ArrayList<GitRemote> remotes = new ArrayList<GitRemote>(repository.getRemotes());

        // Make sure a remote repository is available
        if (!remotes.isEmpty()) {
            String remoteUrl = remotes.iterator().next().getFirstUrl();

            assert remoteUrl != null;
            h.setUrl(remoteUrl);
        }
    }
}
