package gitflow;

import java.util.HashMap;
import java.util.Map;

public class DefaultOptions {
	private static final Map<String, String> options;
	static
	{
		options = new HashMap<String, String>();
		options.put("RELEASE_customTagCommitMessage", "Tagging version %name%");
		options.put("HOTFIX_customHotfixCommitMessage", "Tagging hotfix %name%");
	}

	public static String getOption(String optionId){
		return options.get(optionId);
	}
}
