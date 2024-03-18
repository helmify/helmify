package me.helmify.domain.helm.chart.customizers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Util class for patching in sections of text into a helm template file.
 */
public class TemplateStringPatcher {

	public static int indexOfString(List<String> list, String string) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).contains(string)) {
				return i;
			}
		}
		return -1;
	}

	public static String removeBetween(String startMarker, String stopMarker, String content) {
		final List<String> originalLines = new ArrayList<>(Arrays.asList(content.split("\n")));
		final int startIndex = indexOfString(originalLines, startMarker) + 1;
		final int stopIndex = indexOfString(originalLines, stopMarker) - 1;
		List<String> patched = new ArrayList<>();
		for (int i = 0; i < originalLines.size(); i++) {
			if (i >= startIndex && i <= stopIndex) {
				continue;
			}
			patched.add(originalLines.get(i));
		}
		return String.join("\n", patched);
	}

	public static String insertAfter(String original, String marker, String patch, int leadingSpaces) {
		final List<String> originalLines = new ArrayList<>(Arrays.asList(original.split("\n")));
		final int index = indexOfString(originalLines, marker);
		final String[] split = patch.split("\n");
		final String leadingWhitespace = leadingSpaces > 0 ? " ".repeat(leadingSpaces) : "";

		if (index != -1) {
			int nextIndex = index + 1;
			int newElements = split.length;

			for (int i = 0; i < newElements; i++) {
				originalLines.add(nextIndex + i, leadingWhitespace + split[i]);
			}
		}
		return String.join("\n", originalLines);
	}

}
