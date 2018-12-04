package gitflow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import git4idea.commands.GitCommand;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitImpl;
import git4idea.commands.GitLineHandler;
import git4idea.commands.GitLineHandlerListener;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;

/**
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */

public class GitflowImpl extends GitImpl implements Gitflow {

    //we must use reflection to add this command, since the git4idea implementation doesn't expose it
    private GitCommand GitflowCommand() {
        Method m = null;
        try {
            m = GitCommand.class.getDeclaredMethod("write", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        //m.invoke(d);//exception java.lang.IllegalAccessException
        m.setAccessible(true);//Abracadabra

        GitCommand command = null;

        try {
            command = (GitCommand) m.invoke(null, "flow");//now its ok
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return command;
    }


    private void addOptionsCommand(GitLineHandler h, Project project, String optionId){
        HashMap<String,String> optionMap = GitflowOptionsFactory.getOptionById(optionId);
        if (GitflowConfigurable.isOptionActive(project, optionMap.get("id"))){
            h.addParameters(optionMap.get("flag"));
        }
    }

    public GitCommandResult initRepo(@NotNull GitRepository repository,
                                     GitflowInitOptions initOptions, @Nullable GitLineHandlerListener... listeners) {
        return callInit(repository, initOptions, false, listeners);
    }

    public GitCommandResult reInitRepo(@NotNull GitRepository repository,
            GitflowInitOptions initOptions,
            @Nullable GitLineHandlerListener... listeners) {
        return callInit(repository, initOptions, true, listeners);
    }

    private GitCommandResult callInit(@NotNull GitRepository repository,
            GitflowInitOptions initOptions, boolean reInit,
            @Nullable GitLineHandlerListener[] listeners) {
        GitLineHandler h;
        if (initOptions.isUseDefaults()) {
            h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        } else {
            h = new GitInitLineHandler(initOptions, repository.getProject(),
                    repository.getRoot(), GitflowCommand());
        }

        h.setSilent(false);
        h.setStdoutSuppressed(false);
        h.setStderrSuppressed(false);

        h.addParameters("init");

        if (initOptions.isUseDefaults()) {
            h.addParameters("-d");
        } else {
            for (GitLineHandlerListener listener : listeners) {
                h.addLineListener(listener);
            }
        }

        if (reInit) {
            h.addParameters("-f");
        }

        return runCommand(h);
    }


    //feature

    public GitCommandResult startFeature(@NotNull GitRepository repository,
                                         @NotNull String featureName,
                                         @Nullable String baseBranch,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        h.setSilent(false);

        h.addParameters("feature");
        h.addParameters("start");

        addOptionsCommand(h, repository.getProject(),"FEATURE_fetchFromOrigin");

        h.addParameters(featureName);

        if (baseBranch != null) {
            h.addParameters(baseBranch);
        }

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return runCommand(h);
    }

    public GitCommandResult finishFeature(@NotNull GitRepository repository,
                                          @NotNull String featureName,
                                          @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());

        setUrl(h, repository);
        h.setSilent(false);

        h.addParameters("feature");
        h.addParameters("finish");


        addOptionsCommand(h, repository.getProject(),"FEATURE_keepRemote");
        addOptionsCommand(h, repository.getProject(),"FEATURE_keepLocal");
        addOptionsCommand(h, repository.getProject(),"FEATURE_keepBranch");
        addOptionsCommand(h, repository.getProject(),"FEATURE_fetchFromOrigin");
//        addOptionsCommand(h, repository.getProject(),"FEATURE_squash");

        h.addParameters(featureName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return runCommand(h);
    }


    public GitCommandResult publishFeature(@NotNull GitRepository repository,
                                           @NotNull String featureName,
                                           @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);

        h.addParameters("feature");
        h.addParameters("publish");
        h.addParameters(featureName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return runCommand(h);
    }


    // feature pull seems to be kind of useless. see
    // http://stackoverflow.com/questions/18412750/why-doesnt-git-flow-feature-pull-track
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

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return runCommand(h);
    }

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

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return runCommand(h);
    }


    //release

    public GitCommandResult startRelease(@NotNull GitRepository repository,
                                         @NotNull String releaseName,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("start");

        addOptionsCommand(h, repository.getProject(),"RELEASE_fetchFromOrigin");

        h.addParameters(releaseName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return runCommand(h);
    }

    public GitCommandResult finishRelease(@NotNull GitRepository repository,
                                          @NotNull String releaseName,
                                          @NotNull String tagMessage,
                                          @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("finish");

        addOptionsCommand(h, repository.getProject(),"RELEASE_fetchFromOrigin");
        addOptionsCommand(h, repository.getProject(),"RELEASE_pushOnFinish");
        addOptionsCommand(h, repository.getProject(),"RELEASE_keepRemote");
        addOptionsCommand(h, repository.getProject(),"RELEASE_keepLocal");
        addOptionsCommand(h, repository.getProject(),"RELEASE_keepBranch");
//        addOptionsCommand(h, repository.getProject(),"RELEASE_squash");

        HashMap<String,String> dontTag = GitflowOptionsFactory.getOptionById("RELEASE_dontTag");
        if (GitflowConfigurable.isOptionActive(repository.getProject(), dontTag.get("id"))){
            h.addParameters(dontTag.get("flag"));
        }
        else{
            h.addParameters("-m");
            h.addParameters(tagMessage);
        }

        h.addParameters(releaseName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return runCommand(h);
    }


    public GitCommandResult publishRelease(@NotNull GitRepository repository,
                                           @NotNull String releaseName,
                                           @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);

        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("publish");
        h.addParameters(releaseName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return runCommand(h);
    }

    public GitCommandResult trackRelease(@NotNull GitRepository repository,
                                         @NotNull String releaseName,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("track");
        h.addParameters(releaseName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return runCommand(h);
    }


    //hotfix

    public GitCommandResult startHotfix(@NotNull GitRepository repository,
                                        @NotNull String hotfixName,
                                        @Nullable String baseBranch,
                                        @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        h.setSilent(false);

        h.addParameters("hotfix");
        h.addParameters("start");

        addOptionsCommand(h, repository.getProject(),"HOTFIX_fetchFromOrigin");

        h.addParameters(hotfixName);

        if (baseBranch != null) {
            h.addParameters(baseBranch);
        }

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return runCommand(h);
    }

    public GitCommandResult finishHotfix(@NotNull GitRepository repository,
                                         @NotNull String hotfixName,
                                         @NotNull String tagMessage,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);

        h.addParameters("hotfix");
        h.addParameters("finish");

        addOptionsCommand(h, repository.getProject(),"HOTFIX_fetchFromOrigin");
        addOptionsCommand(h, repository.getProject(),"HOTFIX_pushOnFinish");

        HashMap<String,String> dontTag = GitflowOptionsFactory.getOptionById("HOTFIX_dontTag");
        if (GitflowConfigurable.isOptionActive(repository.getProject(), dontTag.get("id"))){
            h.addParameters(dontTag.get("flag"));
        }
        else{
            h.addParameters("-m");
            h.addParameters(tagMessage);
        }

        h.addParameters(hotfixName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return runCommand(h);
    }

    public GitCommandResult publishHotfix(@NotNull GitRepository repository,
                                          @NotNull String hotfixName,
                                          @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);

        h.setSilent(false);

        h.addParameters("hotfix");
        h.addParameters("publish");
        h.addParameters(hotfixName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return runCommand(h);
    }

    private void setUrl(GitLineHandler h, GitRepository repository) {
        ArrayList<GitRemote> remotes = new ArrayList<GitRemote>(repository.getRemotes());

        //make sure a remote repository is available
        if (!remotes.isEmpty()) {
            h.setUrl(remotes.iterator().next().getFirstUrl());
        }
    }


}
