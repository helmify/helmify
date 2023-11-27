package com.start.helm.cli.arg;

import java.util.Arrays;
import java.util.List;

public class ArgumentListParser {

	public static String getBuildFile(String[] args) {
		List<String> list = Arrays.asList(args);
		if (list.contains("--build-file")) {
			int index = list.indexOf("--build-file");
			return list.get(index + 1);
		}
		return null;
	}

}
