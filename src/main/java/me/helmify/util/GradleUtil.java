package me.helmify.util;

import java.util.function.Function;

public class GradleUtil {

	private static final Function<String, String> splitAndTrim = l -> l.split("=")[1].trim()
		.replaceAll("\"", "")
		.replace("'", "");

	public static String extractName(String buildFile) {
		return buildFile.lines()
			.filter(l -> l.contains("rootProject.name"))
			.map(splitAndTrim)
			.findFirst()
			.orElseThrow();
	}

	public static String extractVersion(String buildFile) {
		return buildFile.lines().filter(l -> l.contains("version = ")).map(splitAndTrim).findFirst().orElseThrow();
	}

}
