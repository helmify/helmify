package com.start.helm.cli.arg;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ArgumentValidator {

	public static boolean isValidArgs(String[] args) {

		if (args.length == 0) {
			printHelp();
			return false;
		}

		final List<String> argumentList = Arrays.asList(args);

		if (argumentList.size() % 2 != 0) {
			if (argumentList.size() == 1 && argumentList.contains("--help")) {
				printHelp();
				return false;
			}
			System.err.println("Invalid number of arguments");
			return false;
		}

		if (argumentList.size() == 2 && argumentList.contains("--build-file")) {
			String path = argumentList.get(1);
			Path buildFilePath = Path.of(path);
			if (!Files.exists(buildFilePath)) {
				System.err.println("Build file at " + buildFilePath.toFile().getAbsolutePath() + " does not exist");
				return false;
			}
			return true;
		}

		return false;
	}

	private static void printHelp() {
		System.out.println("Helm Start CLI");
		System.out.println("Usage: helm-start --key value");
		System.out.println("Options:");
		System.out.println("--build-file <path> Path to the build file (pom.xml/build.gradle)");
		System.out.println("--help Prints this help message");
	}

}
