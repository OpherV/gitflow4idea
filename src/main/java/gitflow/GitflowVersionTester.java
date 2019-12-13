package gitflow;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.util.ExecUtil;
import com.intellij.execution.process.ProcessOutput;

public class GitflowVersionTester {
	static Boolean isSupportedVersion = null;

	static boolean isSupportedVersion(){
		if (isSupportedVersion == null) {

			ProcessOutput output = null;
			GeneralCommandLine commandLine = new GeneralCommandLine();
			commandLine.setExePath("git");
			commandLine.addParameters("flow");
			commandLine.addParameters("version");
			try {
				output = ExecUtil.execAndGetOutput(commandLine);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			String stdout = output.getStdout();
//			System.out.println("output: " + stdout);
			// test that the installed git flow CLI version is AVH and not the unmaintained NVIE version
			isSupportedVersion = stdout.contains("AVH");
		}
		return isSupportedVersion;
	}
}
