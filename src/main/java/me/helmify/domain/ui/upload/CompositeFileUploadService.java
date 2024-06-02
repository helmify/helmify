package me.helmify.domain.ui.upload;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.helmify.domain.build.FileUploadService;
import me.helmify.domain.build.gradle.GradleFileUploadService;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.build.maven.MavenFileUploadService;
import me.helmify.domain.build.maven.MavenModelParser;
import me.helmify.util.GradleUtil;
import org.apache.maven.api.model.Model;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CompositeFileUploadService {

	private final MavenFileUploadService mavenFileUploadService;

	private final GradleFileUploadService gradleFileUploadService;

	@SneakyThrows
	public HelmContext processUpload(MultipartFile file) {
		final String originalFilename = file.getOriginalFilename();
		validateFilename(originalFilename);
		FileUploadService service = chooseService(originalFilename);
		final String buildFile = new String(file.getBytes(), StandardCharsets.UTF_8);
		return service.processBuildFile(buildFile);
	}

	@SneakyThrows
	public HelmContext processBuildfile(String buildFile, String name, String version) {
		String originalFilename = buildFile.contains("<?xml") ? "pom.xml" : "build.gradle";
		FileUploadService service = chooseService(originalFilename);
		return service.processBuildFile(buildFile, name, version);
	}

	private static void validateFilename(String fileName) {
		if (fileName == null) {
			throw new IllegalArgumentException("File name is null");
		}
	}

	private FileUploadService chooseService(String filename) {
		return Stream.of(mavenFileUploadService, gradleFileUploadService)
			.filter(service -> service.shouldHandle(filename))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Unsupported file type: " + filename));
	}

}
