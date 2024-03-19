package me.helmify.initializr;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.FileStoreService;
import me.helmify.domain.events.ChartDownloadedEvent;
import me.helmify.domain.gradle.GradleFileUploadService;
import me.helmify.domain.helm.chart.HelmChartService;
import me.helmify.domain.maven.MavenFileUploadService;
import me.helmify.util.GradleUtil;
import me.helmify.util.ZipUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipInputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitializrSupport {

	private final MavenFileUploadService mavenFileUploadService;

	private final GradleFileUploadService gradleFileUploadService;

	private final HelmChartService helmChartService;

	private final ApplicationEventPublisher publisher;

	private final FileStoreService fileStoreService;

	@SneakyThrows
	public ByteArrayResource repackStarter(byte[] response) {
		String uuid = UUID.randomUUID().toString();

		String tmpDir = fileStoreService.getTmpDirectory().toFile().getAbsolutePath();
		File parentDir = Paths.get(tmpDir, uuid).toFile();
		parentDir.mkdirs();

		File helmDir = Paths.get(parentDir.getAbsolutePath(), "helm").toFile();
		helmDir.mkdirs();

		org.zeroturnaround.zip.ZipUtil.unpack(new ByteArrayInputStream(response), parentDir);

		ByteArrayResource helmChart = this.generateHelmChart(new ByteArrayResource(response));

		org.zeroturnaround.zip.ZipUtil.unpack(helmChart.getInputStream(), helmDir);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(response.length);

		boolean hasSrcDir = !Arrays.stream(parentDir.list((dir, name) -> name.equals("src"))).toList().isEmpty();
		if (!hasSrcDir) {

			Arrays.stream(parentDir.listFiles())
				.filter(f -> f.isDirectory() && !f.getName().equals("helm"))
				.forEach(f -> {
					try {
						Files.move(helmDir.toPath(), Paths.get(f.getAbsolutePath(), "helm"));
					}
					catch (IOException e) {
						log.error("Error moving directory", e);
					}
				});
		}

		org.zeroturnaround.zip.ZipUtil.pack(parentDir, outputStream);

		ByteArrayResource merged = new ByteArrayResource(outputStream.toByteArray());

		publisher.publishEvent(new ChartDownloadedEvent());

		return merged;
	}

	@SneakyThrows
	public ByteArrayResource generateHelmChart(ByteArrayResource body) {
		byte[] content = body.getContentAsByteArray();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(content.length);

		Optional<String> buildFile = tryReadBuildFile(body.getInputStream());
		buildFile.map(f -> {
			if (f.trim().startsWith("<?xml")) {
				return mavenFileUploadService.processBuildFile(f);
			}
			else {
				try {
					String appName = getGradleAppName(body.getInputStream());
					return gradleFileUploadService.processBuildFile(f, appName, GradleUtil.extractVersion(f));
				}
				catch (Exception e) {
					log.error("Error processing build file", e);
				}
				return null;
			}
		}).map(helmContext -> this.helmChartService.process(helmContext, outputStream, true)).orElseThrow();

		return new ByteArrayResource(outputStream.toByteArray());
	}

	private String getGradleAppName(InputStream inputStream) {
		resetStream(inputStream);
		Optional<String> zipContent = ZipUtil.getZipContent("settings.gradle", new ZipInputStream(inputStream));
		return zipContent.map(s -> s.replace("rootProject.name = ", ""))
			.map(InitializrSupport::cleanupAppName)
			.orElse("my-project");
	}

	private static String cleanupAppName(String name) {
		return name.replace("'", "")
			.replaceAll("[^\\x00-\\x7F]", "")
			.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")
			.replaceAll("\\p{C}", "");
	}

	@SneakyThrows
	private void resetStream(InputStream zipInputStream) {
		try {
			zipInputStream.reset();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Optional<String> tryReadBuildFile(InputStream zipInputStream) {
		return Optional
			.ofNullable(ZipUtil.getZipContent("build.gradle.kts", new ZipInputStream(zipInputStream)).orElseGet(() -> {
				resetStream(zipInputStream);
				return ZipUtil.getZipContent("build.gradle", new ZipInputStream(zipInputStream)).orElseGet(() -> {
					resetStream(zipInputStream);
					return ZipUtil.getZipContent("pom.xml", new ZipInputStream(zipInputStream)).orElse(null);
				});
			}));
	}

}
