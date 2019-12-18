package gitflow;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GitflowVersionTester {

	private static final Logger logger = Logger.getInstance(GitflowVersionTester.class);

	private static final Map<Project, GitflowVersionTester> testers = new ConcurrentHashMap<>();

	public static GitflowVersionTester forProject(Project project) {
		// Java 8
//		return testers.computeIfAbsent(
//			project,
//			p -> new GitflowVersionTester(ServiceManager.getService(Gitflow.class), p)
//		);
		GitflowVersionTester ret = testers.get(project);
		if (ret == null) {
			ret = new GitflowVersionTester(ServiceManager.getService(Gitflow.class), project);
			testers.put(project, ret);
		}

		return ret;
	}

	private final Gitflow gitflow;
	private final Project project;

	private String version;

	private GitflowVersionTester(Gitflow gitflow, Project project) {
		this.gitflow = gitflow;
		this.project = project;
	}

	public String getVersion() {
		if (version == null) {
			try {
				version = gitflow.version(project).getOutputOrThrow();
				logger.info("git flow version: " + version);
			} catch (Exception e) {
				logger.error("Could not determine git flow version", e);
			}
		}

		return version;
	}

	public boolean isSupportedVersion() {
		String s = getVersion();
		return s != null && s.contains("AVH");
	}
}
