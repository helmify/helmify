package com.start.helm.domain.ui;

import com.start.helm.domain.ui.model.BuildInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Properties;

/**
 * Component reading git.properties (from git-commit-id-plugin) and exposing the build info.
 */
@Slf4j
@Component
public class BuildInfoProvider {

    private Properties loadBuildInfo() {
        Properties properties = new Properties();
        ClassPathResource classPathResource = new ClassPathResource("git.properties");
        try (InputStream inputStream = classPathResource.getInputStream()) {
            properties.load(inputStream);
        } catch (Exception e) {
            log.warn("Could not load build info", e);
        }
        return properties;
    }

    public BuildInfo getBuildInfo() {
        BuildInfo buildInfo = new BuildInfo();

        Properties properties = loadBuildInfo();
        if (properties.containsKey("git.build.version")) {
            buildInfo.setVersion(properties.getProperty("git.build.version"));
        } else {
            buildInfo.setVersion("N/A");
        }
        if (properties.containsKey("git.commit.id.abbrev")) {
            buildInfo.setId(properties.getProperty("git.commit.id.abbrev"));
        } else {
            buildInfo.setId("N/A");
        }
        addBuildTime(buildInfo, properties);

        return buildInfo;
    }

    private static void addBuildTime(BuildInfo buildInfo, Properties properties) {
        if (properties.containsKey("git.build.time")) {
            OffsetDateTime parse = OffsetDateTime.parse(properties.getProperty("git.build.time").replace("0200", "02:00"));
            buildInfo.setDate(Date.from(parse.toInstant()));
        } else {
            buildInfo.setDate(new Date(0));
        }
    }

}
