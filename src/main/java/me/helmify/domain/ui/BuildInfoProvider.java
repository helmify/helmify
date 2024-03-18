package me.helmify.domain.ui;

import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.ui.model.BuildInfo;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Properties;

/**
 * Component reading git.properties (from git-commit-id-plugin) and exposing the build
 * info.
 */
@Slf4j
@Component
public class BuildInfoProvider {

	private Properties loadBuildInfo() {
		Properties properties = new Properties();
		ClassPathResource classPathResource = new ClassPathResource("git.properties");
		try (InputStream inputStream = classPathResource.getInputStream()) {
			properties.load(inputStream);
		}
		catch (Exception e) {
			log.warn("Could not load build info", e);
		}
		return properties;
	}

	public BuildInfo getBuildInfo() {
		BuildInfo buildInfo = new BuildInfo();

		Properties properties = loadBuildInfo();
		if (properties.containsKey("git.build.version")) {
			buildInfo.setVersion(properties.getProperty("git.build.version"));
		}
		else {
			buildInfo.setVersion("N/A");
		}
		if (properties.containsKey("git.commit.id.abbrev")) {
			buildInfo.setId(properties.getProperty("git.commit.id.abbrev"));
		}
		else {
			buildInfo.setId("N/A");
		}
		addBuildTime(buildInfo, properties);

		return buildInfo;
	}

	private static void addBuildTime(BuildInfo buildInfo, Properties properties) {
		if (properties.containsKey("git.build.time")) {
			String p = properties.getProperty("git.build.time");

			// fix weird +0000 timezone format
			if (p.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+(\\d{4})")) {
				p = new StringBuffer(p).insert(p.length() - 2, ":").toString();
			}

			OffsetDateTime parse = OffsetDateTime.parse(p);
			buildInfo.setDate(Date.from(parse.toInstant()));
		}
		else {
			buildInfo.setDate(new Date(0));
		}
	}

}
