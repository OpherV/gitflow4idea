package gitflow;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GitflowVersionTester {

	private static final Logger logger = Logger.getInstance(GitflowVersionTester.class);

	private static final Map<Project, GitflowVersionTester> testers = new ConcurrentHashMap<>();

	public static GitflowVersionTester forProject(@NotNull Project project) {
		return testers.computeIfAbsent(
			project,
			p -> {
				Disposer.register(p, () -> testers.remove(p));
				return new GitflowVersionTester(ServiceManager.getService(Gitflow.class), p);
			}
		);
	}

	@NotNull private final Gitflow gitflow;
	@NotNull private final Project project;

	private String version;

	private GitflowVersionTester(@NotNull Gitflow gitflow, @NotNull Project project) {
		this.gitflow = gitflow;
		this.project = project;
	}

	/**
	 * <p>Returns the installed {@code git-flow} version. The version
	 * is loaded on the first call to this method and is determined
	 * by looking at the output of a {@code git flow version} command.</p>
	 * <p>If the command fails, {@code null} is returned.</p>
	 *
	 * @return the {@code git flow} version, or {@code null}
	 */
	@Nullable
	public String getVersion() {
		return version;
	}

	/**
	 * Returns true if the {@code git flow} version can be determined
	 * and is any AVH version ({@code #contains("AVH")}) and
	 * not the unmaintained NVIE version.
	 *
	 * @return true if we think the git flow version is an AVH version.
	 */
	public boolean isSupportedVersion() {
		return version != null && version.contains("AVH");
	}

	public void init(){
		String returnedVersion = null;
		try {
			returnedVersion = gitflow.version(project).getOutputOrThrow();
			logger.info("git flow version: " + version);
		} catch (Exception e) {
			logger.error("Could not determine git flow version", e);
		}
		if (returnedVersion != null){
			version = returnedVersion;
		}
	}
}
