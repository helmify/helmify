package me.helmify.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileUtil {

	public static void createPathIfNotExists(Path path) {
		if (!Files.exists(path)) {
			try {
				Files.createDirectory(path);
			}
			catch (Exception e) {
				log.error("Error creating instance path", e);
			}
		}
	}

}
