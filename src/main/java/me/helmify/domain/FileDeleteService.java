package me.helmify.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
public class FileDeleteService {

	@Value("${helm-start.data-directory:helm-start-data}")
	private String dataDirectory;

	@Scheduled(fixedDelay = 1000 * 60 * 60)
	public void deleteFiles() {
		long now = System.currentTimeMillis();
		long hourAgo = now - 1000 * 60 * 60;

		try {
			List<File> toDelete = Files.list(Paths.get(dataDirectory, "tmp"))
				.map(Path::toFile)
				.filter(f -> f.lastModified() < hourAgo)
				.toList();

			if (!toDelete.isEmpty()) {
				log.info("Deleting {} files", toDelete.size());
				toDelete.forEach(File::delete);
			}
		}
		catch (Exception e) {
			log.error("Error deleting files", e);
		}

	}

}
