package com.start.helm.domain.ui;

import com.start.helm.domain.gradle.GradleFileUploadService;
import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.HelmChartService;
import com.start.helm.domain.maven.MavenFileUploadService;
import com.start.helm.util.DownloadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

	@PostMapping("/cli")
	public Object cli(@RequestParam("name") String name, @RequestParam("version") String version,
			@RequestBody String buildFileContents) {

		log.info("Got request for name: {}", name);
		log.info("Got request for version: {}", version);
		log.info("Got request for buildFileContents: {}", buildFileContents);

		HelmContext helmContext = buildFileContents.contains("<groupId>")
				? mavenFileUploadService.processBuildFile(buildFileContents)
				: gradleFileUploadService.processBuildFile(buildFileContents);

		byte[] resource = helmChartService.process(helmContext);
		return ResponseEntity.ok()
			.headers(DownloadUtil.headers("helm.zip"))
			.contentLength(resource.length)
			.contentType(MediaType.parseMediaType("application/octet-stream"))
			.body(resource);
	}

}
