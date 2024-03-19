package me.helmify.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileDeleteService {

	private final FileStoreService fileStoreService;

	@Scheduled(fixedDelay = 1000 * 60 * 60)
	public void deleteFiles() {
		long now = System.currentTimeMillis();
		long hourAgo = now - 1000 * 60 * 60;

		Path tmpDir = fileStoreService.getTmpDirectory();

		try (Stream<Path> toDelete = Files.list(tmpDir)) {

			toDelete.map(Path::toFile).filter(f -> f.lastModified() < hourAgo).forEach(f -> {
				boolean delete = f.delete();
				log.info("Deleted file {}: {}", f.getName(), delete);
			});

		}
		catch (Exception e) {
			log.error("Error deleting files", e);
		}
	}

}
