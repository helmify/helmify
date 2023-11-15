package com.start.helm.cli.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AppNameProvider {

	public String getAppName(String buildFile) {

		if (buildFile.contains("pom.xml")) {
			return extractNameFromPom(buildFile);
		}

		if (buildFile.contains("build.gradle")) {
			return extractNameFromGradle(buildFile);
		}

		return "app";
	}

	private String extractNameFromGradle(String buildFile) {
		Path buildFileDir = Path.of(buildFile).getParent();
		Path settingsPath = Path.of(buildFileDir.toFile().getAbsolutePath(), "settings.gradle");
		try {
			List<String> strings = Files.readAllLines(settingsPath);
			for (String string : strings) {
				if (string.contains("rootProject.name")) {
					return string.replace("rootProject.name", "")
						.replace("=", "")
						.replace("\"", "")
						.replace("'", "")
						.trim();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "app";
	}

	private String extractNameFromPom(String buildFile) {
		try {
			List<String> strings = Files.readAllLines(Path.of(buildFile));
			boolean hasParent = strings.stream().anyMatch(s -> s.contains("<parent>"));
			List<String> artifactIds = strings.stream().filter(s -> s.contains("<artifactId>")).toList();
			final String artifactId = hasParent ? artifactIds.get(1) : artifactIds.get(0);
			return artifactId.replace("<artifactId>", "").replace("</artifactId>", "").trim();
		}
		catch (Exception e) {
			System.err.println("error reading buildfile at " + buildFile);
			e.printStackTrace();
		}
		return "app";
	}

}
