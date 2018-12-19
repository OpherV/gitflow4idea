package gitflow;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GitflowOptionsFactory {
    private static final GitflowOptionsFactory instance = new GitflowOptionsFactory();
    private Map<Enum<TYPE>, ArrayList<Map<String,String>>> options;
    private HashMap<String, HashMap<String,String>> optionsMap;

    public enum TYPE {
        FEATURE, RELEASE, HOTFIX, BUGFIX
    }

    //private constructor to avoid client applications to use constructor
    private GitflowOptionsFactory(){
        options = new HashMap<Enum<TYPE>, ArrayList<Map<String,String>>>();
        optionsMap = new HashMap<String, HashMap<String,String>>();

        addBranchType(TYPE.FEATURE);
        addOption(TYPE.FEATURE, "Fetch from Origin", "fetchFromOrigin" , "-F");
        addOption(TYPE.FEATURE, "Keep Local", "keepLocal", "--keeplocal");
        addOption(TYPE.FEATURE, "Keep Remote", "keepRemote", "--keepremote");
        addOption(TYPE.FEATURE, "Keep branch after performing finish", "keepBranch" , "-k");
        addOption(TYPE.FEATURE, "Do not fast-forward when merging, always create commit", "noFastForward" , "--no-ff");
//        addOption(TYPE.FEATURE, "Squash feature during merge", "squash" , "-S");

        addBranchType(TYPE.RELEASE);
        addOption(TYPE.RELEASE, "Fetch from Origin", "fetchFromOrigin" , "-F");
        addOption(TYPE.RELEASE, "Push on finish release", "pushOnFinish" , "-p");
        addOption(TYPE.RELEASE, "Keep Local", "keepLocal", "--keeplocal");
        addOption(TYPE.RELEASE, "Keep Remote", "keepRemote", "--keepremote");
        addOption(TYPE.RELEASE, "Keep branch after performing finish", "keepBranch" , "-k");
//        addOption(TYPE.RELEASE, "Squash release during merge", "squash" , "-S");
        addOption(TYPE.RELEASE, "Don't tag release", "dontTag" , "-n");
        addOption(TYPE.RELEASE, "Use custom tag commit message", "customTagCommitMessage" , null, DefaultOptions.getOption("RELEASE_customTagCommitMessage") ,"Use %name% for the branch name");

        addBranchType(TYPE.HOTFIX);
        addOption(TYPE.HOTFIX, "Fetch from Origin", "fetchFromOrigin" , "-F");
        addOption(TYPE.HOTFIX, "Push on finish Hotfix", "pushOnFinish" , "-p");
        addOption(TYPE.HOTFIX, "Don't tag Hotfix", "dontTag" , "-n");
        addOption(TYPE.HOTFIX, "Use custom hotfix commit message", "customHotfixCommitMessage" , null, DefaultOptions.getOption("HOTFIX_customHotfixCommitMessage") ,"Use %name% for the branch name");

        addBranchType(TYPE.BUGFIX);
        addOption(TYPE.BUGFIX, "Fetch from Origin", "fetchFromOrigin" , "-F");
        addOption(TYPE.BUGFIX, "Keep Local", "keepLocal", "--keeplocal");
        addOption(TYPE.BUGFIX, "Keep Remote", "keepRemote", "--keepremote");
        addOption(TYPE.BUGFIX, "Keep branch after performing finish", "keepBranch" , "-k");
        addOption(TYPE.BUGFIX, "Do not fast-forward when merging, always create commit", "noFastForward" , "--no-ff");
//        addOption(TYPE.BUGFIX, "Squash feature during merge", "squash" , "-S");
    }

    private void addBranchType(Enum<TYPE> branchType){
        options.put(branchType, new ArrayList<Map<String, String>>());
    }

    private void addOption(Enum<TYPE> branchType, String description, String key, @Nullable String flag){
        addOption(branchType, description, key, flag, null, null);
    }

    private void addOption(Enum<TYPE> branchType, String description, String key, @Nullable String flag, @Nullable String inputText, @Nullable String toolTip){
        HashMap<String, String> optionMap = new HashMap<String, String>();
        optionMap.put("description", description);
        optionMap.put("key", key);
        if (flag != null){
            optionMap.put("flag", flag);
        }
        if (inputText != null){
            optionMap.put("inputText", inputText);
        }
        if (toolTip != null){
            optionMap.put("toolTip", toolTip);
        }

        String optionId = getOptionId(branchType, key);
        optionMap.put("id", optionId);
        optionsMap.put(optionId, optionMap);

        options.get(branchType).add(optionMap);
    }

    public static  Map<Enum<TYPE>, ArrayList<Map<String,String>>> getOptions(){
        return instance.options;
    }

    public static String getOptionId(Enum<TYPE> branchType, String key){
        return  branchType+"_"+key;
    }

    public static HashMap<String,String> getOptionById(String optionId){
        return instance.optionsMap.get(optionId);
    }
}
