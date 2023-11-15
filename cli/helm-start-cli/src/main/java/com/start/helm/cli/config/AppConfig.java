package com.start.helm.cli.config;

public class AppConfig {

    private final String buildFile;
    private final String appName;
    private final String appVersion;

    public AppConfig(String buildFile, String appName, String appVersion) {
        this.buildFile = buildFile;
        this.appName = appName;
        this.appVersion = appVersion;
    }

    public String getBuildFile() {
        return buildFile;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }
}
