package gitflowavh;


public class GitFlowAVHInitOptions {
    private boolean useDefaults;
    private String productionBranch;
    private String developmentBranch;
    private String featurePrefix;
    private String releasePrefix;
    private String hotfixPrefix;
    private String supportPrefix;
    private String versionPrefix;

    /**
     * @return boolean
     */
    public boolean isUseDefaults() {
        return useDefaults;
    }

    /**
     * @param useDefaults boolean
     */
    public void setUseDefaults(boolean useDefaults) {
        this.useDefaults = useDefaults;
    }

    /**
     * @return String
     */
    public String getProductionBranch() {
        return productionBranch;
    }

    /**
     * @param productionBranch String
     */
    public void setProductionBranch(String productionBranch) {
        this.productionBranch = productionBranch;
    }

    /**
     * @return String
     */
    public String getDevelopmentBranch() {
        return developmentBranch;
    }

    /**
     * @param developmentBranch String
     */
    public void setDevelopmentBranch(String developmentBranch) {
        this.developmentBranch = developmentBranch;
    }

    /**
     * @return String
     */
    public String getFeaturePrefix() {
        return featurePrefix;
    }

    /**
     * @param featurePrefix String
     */
    public void setFeaturePrefix(String featurePrefix) {
        this.featurePrefix = featurePrefix;
    }

    /**
     * @return String
     */
    public String getReleasePrefix() {
        return releasePrefix;
    }

    /**
     * @param releasePrefix String
     */
    public void setReleasePrefix(String releasePrefix) {
        this.releasePrefix = releasePrefix;
    }

    /**
     * @return String
     */
    public String getHotfixPrefix() {
        return hotfixPrefix;
    }

    /**
     * @param hotfixPrefix String
     */
    public void setHotfixPrefix(String hotfixPrefix) {
        this.hotfixPrefix = hotfixPrefix;
    }

    /**
     * @return String
     */
    public String getSupportPrefix() {
        return supportPrefix;
    }

    /**
     * @param supportPrefix String
     */
    public void setSupportPrefix(String supportPrefix) {
        this.supportPrefix = supportPrefix;
    }

    /**
     * @return String
     */
    public String getVersionPrefix() {
        return versionPrefix;
    }

    /**
     * @param versionPrefix String
     */
    public void setVersionPrefix(String versionPrefix) {
        this.versionPrefix = versionPrefix;
    }
}
