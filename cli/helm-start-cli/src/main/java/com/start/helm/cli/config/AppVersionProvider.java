package com.start.helm.cli.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AppVersionProvider {
    public String getAppVersion(String buildFile) {

        if(buildFile.contains("build.gradle")) {
            return extractVersionFromGradle(buildFile);
        }

        if(buildFile.contains("pom.xml")) {
            return  extractVersionFromPom(buildFile);
        }

        return"";
    }

    private String extractVersionFromPom(String buildFile) {
        try {
            List<String> strings = Files.readAllLines(Path.of(buildFile));
            for (String string : strings) {
                if(string.contains("<version>")) {
                    return string.replace("<version>", "").replace("</version>", "").trim();
                }
            }
        } catch (Exception e) {
            System.err.println("error reading buildfile at " + buildFile);
            e.printStackTrace();
        }
        return "1.0.0";
    }

    private String extractVersionFromGradle(String buildFile) {
        try {
            List<String> strings = Files.readAllLines(Path.of(buildFile));
            for (String string : strings) {
                if(string.startsWith("version")) {
                    return string.replace("version", "").replace("=", "").replace("'", "").replace("\"", "").trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }
}
