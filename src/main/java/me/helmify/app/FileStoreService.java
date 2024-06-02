package me.helmify.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.ui.counter.ChartCounter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static me.helmify.util.FileUtil.createPathIfNotExists;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStoreService {

	@Value("${helmify.data-directory:helmify-data}")
	private String dataDirectory;

	@Value("${helmify.instance:dev}")
	private String instance;

	private final ObjectMapper objectMapper;

	public Path getInstancePath() {
		Path path = Paths.get(getDataDirectory().toFile().getAbsolutePath(), instance);
		createPathIfNotExists(path);
		return path;
	}

	public Path getDataDirectory() {
		Path path = Paths.get(dataDirectory);
		createPathIfNotExists(path);
		return path;
	}

	public Path getTmpDirectory() {
		Path path = Paths.get(getInstancePath().toFile().getAbsolutePath(), "tmp");
		createPathIfNotExists(path);
		return path;
	}

	public File getStore() {
		try {

			Path filePath = Paths.get(getInstancePath().toFile().getAbsolutePath(),
					"chart-count-" + instance + ".json");
			if (!Files.exists(filePath)) {
				Files.createFile(filePath);
				String json = this.objectMapper.writeValueAsString(new ChartCounter.ChartCount(0));
				Files.write(filePath, json.getBytes());
			}

			return filePath.toFile();
		}
		catch (Exception e) {
			log.error("Error getting store", e);
		}
		return null;
	}

}
