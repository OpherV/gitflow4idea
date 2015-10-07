package gitflowavh;


public class GitFlowAVHInitOptions {
    private boolean useDefaults;
    private String productionBranch;
    private String developmentBranch;
    private String featurePrefix;
    private String releasePrefix;
    private String hotfixPrefix;
    private String bugfixPrefix;
    private String supportPrefix;
    private String versionPrefix;


    public boolean isUseDefaults() {
        return useDefaults;
    }

    public void setUseDefaults(boolean useDefaults) {
        this.useDefaults = useDefaults;
    }

    public String getProductionBranch() {
        return productionBranch;
    }

    public void setProductionBranch(String productionBranch) {
        this.productionBranch = productionBranch;
    }

    public String getDevelopmentBranch() {
        return developmentBranch;
    }

    public void setDevelopmentBranch(String developmentBranch) {
        this.developmentBranch = developmentBranch;
    }

    public String getFeaturePrefix() {
        return featurePrefix;
    }

    public void setFeaturePrefix(String featurePrefix) {
        this.featurePrefix = featurePrefix;
    }

    public String getBugfixPrefix() {
        return bugfixPrefix;
    }

    public void setBugfixPrefix(String bugfixPrefix) {
        this.bugfixPrefix = bugfixPrefix;
    }

    public String getReleasePrefix() {
        return releasePrefix;
    }

    public void setReleasePrefix(String releasePrefix) {
        this.releasePrefix = releasePrefix;
    }

    public String getHotfixPrefix() {
        return hotfixPrefix;
    }

    public void setHotfixPrefix(String hotfixPrefix) {
        this.hotfixPrefix = hotfixPrefix;
    }

    public String getSupportPrefix() {
        return supportPrefix;
    }

    public void setSupportPrefix(String supportPrefix) {
        this.supportPrefix = supportPrefix;
    }

    public String getVersionPrefix() {
        return versionPrefix;
    }

    public void setVersionPrefix(String versionPrefix) {
        this.versionPrefix = versionPrefix;
    }
}
