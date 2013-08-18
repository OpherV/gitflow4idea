package git4idea.commands;

import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Opher
 * Date: 8/18/13
 * Time: 5:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Gitflow extends Git {

    GitCommandResult startFeature(@NotNull GitRepository repository,
                                  @NotNull String featureName,
                                  @NotNull GitLineHandlerListener... listeners);

}
