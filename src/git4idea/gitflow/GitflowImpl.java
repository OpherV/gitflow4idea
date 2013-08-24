package git4idea.gitflow;

import git4idea.commands.*;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 *
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */
public class GitflowImpl extends GitImpl implements Gitflow {

    public GitCommandResult initRepo(@NotNull GitRepository repository,
                                     @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.FLOW);
        h.setSilent(false);

        h.addParameters("init");
        h.addParameters("-d");

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }


    //feature

    public GitCommandResult startFeature(@NotNull GitRepository repository,
                                         @NotNull String featureName,
                                         @Nullable GitLineHandlerListener... listeners) {
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

    public GitCommandResult finishFeature(@NotNull GitRepository repository,
                                         @NotNull String featureName,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitCommand.FLOW);
        h.setUrl(repository.getRemotes().iterator().next().getFirstUrl());
        h.setSilent(false);

        h.addParameters("feature");
        h.addParameters("finish");
        h.addParameters(featureName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }


    public GitCommandResult publishFeature(@NotNull GitRepository repository,
                                          @NotNull String featureName,
                                          @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitCommand.FLOW);
        h.setUrl(repository.getRemotes().iterator().next().getFirstUrl());
        h.setSilent(false);

        h.addParameters("feature");
        h.addParameters("publish");
        h.addParameters(featureName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    public GitCommandResult pullFeature(@NotNull GitRepository repository,
                                           @NotNull String featureName,
                                           @NotNull GitRemote remote,
                                           @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitCommand.FLOW);
        h.setUrl(remote.getFirstUrl());
        h.setSilent(false);
        h.addParameters("feature");
        h.addParameters("pull");
        h.addParameters(remote.getName());
        h.addParameters(featureName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }


    //release

    public GitCommandResult startRelease(@NotNull GitRepository repository,
                                         @NotNull String releaseName,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.FLOW);
        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("start");
        h.addParameters(releaseName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    public GitCommandResult finishRelease(@NotNull GitRepository repository,
                                          @NotNull String releaseName,
                                          @NotNull String tagMessage,
                                          @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitCommand.FLOW);
        h.setUrl(repository.getRemotes().iterator().next().getFirstUrl());
        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("finish");
        h.addParameters("-m");
        h.addParameters(StringEscapeUtils.escapeJava(tagMessage));
        h.addParameters(releaseName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }


    public GitCommandResult publishRelease(@NotNull GitRepository repository,
                                           @NotNull String releaseName,
                                           @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitCommand.FLOW);
        h.setUrl(repository.getRemotes().iterator().next().getFirstUrl());

        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("publish");
        h.addParameters(releaseName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    public GitCommandResult trackRelease(@NotNull GitRepository repository,
                                        @NotNull String releaseName,
                                        @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitCommand.FLOW);
        h.setUrl(repository.getRemotes().iterator().next().getFirstUrl());
        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("track");
        h.addParameters(releaseName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }


    //hotfix

    public GitCommandResult startHotfix(@NotNull GitRepository repository,
                                         @NotNull String hotfixName,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.FLOW);
        h.setSilent(false);

        h.addParameters("hotfix");
        h.addParameters("start");
        h.addParameters(hotfixName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    public GitCommandResult finishHotfix(@NotNull GitRepository repository,
                                          @NotNull String hotfixName,
                                          @NotNull String tagMessage,
                                          @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitCommand.FLOW);
        h.setUrl(repository.getRemotes().iterator().next().getFirstUrl());
        h.setSilent(false);

        h.addParameters("hotfix");
        h.addParameters("finish");
        h.addParameters("-m");
        h.addParameters(StringEscapeUtils.escapeJava(tagMessage));
        h.addParameters(hotfixName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

}
