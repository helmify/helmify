package me.helmify.domain.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.events.ChartDownloadedEvent;
import me.helmify.domain.gradle.GradleFileUploadService;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.chart.HelmChartService;
import me.helmify.domain.maven.MavenFileUploadService;
import me.helmify.util.DownloadUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CliApi {

	private final HelmChartService helmChartService;

	private final MavenFileUploadService mavenFileUploadService;

	private final GradleFileUploadService gradleFileUploadService;

	private final ApplicationEventPublisher publisher;

	@PostMapping("/cli")
	public ResponseEntity<byte[]> cli(@RequestParam("name") String name, @RequestParam("version") String version,
			@RequestBody String buildFileContents) {

		HelmContext helmContext = buildFileContents.contains("<groupId>")
				? mavenFileUploadService.processBuildFile(buildFileContents, name, version)
				: gradleFileUploadService.processBuildFile(buildFileContents, name, version);

		byte[] resource = helmChartService.process(helmContext);
		publisher.publishEvent(new ChartDownloadedEvent());

		return ResponseEntity.ok()
			.headers(DownloadUtil.headers("helm.zip"))
			.contentLength(resource.length)
			.contentType(MediaType.parseMediaType("application/octet-stream"))
			.body(resource);
	}

}
