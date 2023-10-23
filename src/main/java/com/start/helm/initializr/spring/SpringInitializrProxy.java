package com.start.helm.initializr.spring;

import com.start.helm.domain.events.ChartDownloadedEvent;
import com.start.helm.domain.gradle.GradleFileUploadService;
import com.start.helm.domain.helm.chart.HelmChartService;
import com.start.helm.domain.maven.MavenFileUploadService;
import com.start.helm.util.DownloadUtil;
import com.start.helm.util.ZipUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SpringInitializrProxy {

	private final MavenFileUploadService mavenFileUploadService;

	private final GradleFileUploadService gradleFileUploadService;

	private final HelmChartService helmChartService;

	private final ApplicationEventPublisher publisher;

	@GetMapping(value = "/spring")
	public Object getCapabilities() {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(List.of(MediaType.valueOf("application/vnd.initializr.v2.2+json")));
		HttpEntity<Object> entity = new HttpEntity<>(headers);

		return restTemplate.exchange("https://start.spring.io/", HttpMethod.GET, entity, Object.class);
	}

	@GetMapping(value = "/spring/starter.zip")
	public ResponseEntity<?> getStarter(HttpServletRequest request) throws IOException {

		RestTemplate restTemplate = new RestTemplate();

		Map<String, List<String>> collected = request.getParameterMap()
			.keySet()
			.stream()
			.collect(Collectors.toMap(k -> k, k -> Arrays.asList(request.getParameterMap().get(k))));

		URI uri = UriComponentsBuilder.fromHttpUrl("https://start.spring.io/starter.zip")
			.queryParams(new MultiValueMapAdapter<>(collected))
			.build()
			.toUri();

		ResponseEntity<byte[]> forEntity = restTemplate.getForEntity(uri, byte[].class);

		if (forEntity.getStatusCode().is2xxSuccessful() && forEntity.getBody() != null) {

			byte[] body = forEntity.getBody();
			String uuid = UUID.randomUUID().toString();
			Path dataDirectory = Paths.get("helm-start-data");
			File parentDir = Paths.get(dataDirectory.toFile().getAbsolutePath(), "tmp", uuid).toFile();
			parentDir.mkdirs();
			File helmDir = Paths.get(parentDir.getAbsolutePath(), "helm").toFile();
			helmDir.mkdirs();

			org.zeroturnaround.zip.ZipUtil.unpack(new ByteArrayInputStream(body), parentDir);

			ByteArrayResource helmChart = this.generateHelmChart(new ByteArrayResource(body));

			org.zeroturnaround.zip.ZipUtil.unpack(helmChart.getInputStream(), helmDir);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(body.length);

			org.zeroturnaround.zip.ZipUtil.pack(parentDir, outputStream);

			ByteArrayResource merged = new ByteArrayResource(outputStream.toByteArray());

			publisher.publishEvent(new ChartDownloadedEvent());

			return ResponseEntity.ok()
				.headers(DownloadUtil.headers("starter.zip"))
				.contentLength(merged.contentLength())
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(merged);
		}

		return ResponseEntity.internalServerError().build();
	}

	@SneakyThrows
	private ByteArrayResource generateHelmChart(ByteArrayResource body) {
		byte[] content = body.getContentAsByteArray();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(content.length);

		Optional<String> buildFile = tryReadBuildFile(body.getInputStream());
		buildFile
			.map(f -> f.trim().startsWith("<?xml") ? mavenFileUploadService.processBuildFile(f)
					: gradleFileUploadService.processBuildFile(f))
			.map(helmContext -> this.helmChartService.process(helmContext, outputStream, true))
			.orElseThrow();

		return new ByteArrayResource(outputStream.toByteArray());
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
