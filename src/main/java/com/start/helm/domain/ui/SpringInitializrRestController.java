package com.start.helm.domain.ui;

import com.start.helm.domain.gradle.GradleFileUploadService;
import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.maven.MavenFileUploadService;
import com.start.helm.util.GradleUtil;
import com.start.helm.util.ZipUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SpringInitializrRestController {

	private final RestTemplate restTemplate;

	private final MavenFileUploadService mavenFileUploadService;

	private final GradleFileUploadService gradleFileUploadService;

	@Getter
	@Setter
	@ToString
	public static class SpringInitializrRequest {

		private String springInitializrLink;

	}

	@SneakyThrows
	@PostMapping("/spring-initializr-link")
	public String generate(Model viewModel, @RequestBody SpringInitializrRequest request) {
		log.info("Generate request: {}", request);

		final String springInitializrLink = request.getSpringInitializrLink();
		validateLink(springInitializrLink);

		final String zipLink = springInitializrLink.replace("https://start.spring.io/#!",
				"https://start.spring.io/starter.zip?");

		ResponseEntity<byte[]> response = restTemplate.getForEntity(zipLink, byte[].class);
		log.info("Received Response, Code {}", response.getStatusCode());
		validateResponseCode(response);

		boolean isUsingKotlinScript = springInitializrLink.contains("gradle-project-kotlin");

		byte[] body = response.getBody();

		HelmContext helmContext = Stream
			.of(new GradleBuildProcessor(body, gradleFileUploadService, isUsingKotlinScript),
					new MavenBuildProcessor(body, mavenFileUploadService))
			.filter(processor -> springInitializrLink.contains(processor.matchOn()))
			.findFirst()
			.map(BuildProcessor::process)
			.orElseThrow();

		viewModel.addAttribute("helmContext", helmContext);
		viewModel.addAttribute("springInitializrLink", springInitializrLink);

		return "fragments :: pom-upload-form";
	}

	private static void validateResponseCode(ResponseEntity<byte[]> response) {
		if (!response.getStatusCode().is2xxSuccessful()) {
			throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,
					"Could not download zip");
		}
	}

	interface BuildProcessor {

		HelmContext process();

		String matchOn();

	}

	@RequiredArgsConstructor
	static class GradleBuildProcessor implements BuildProcessor {

		private final byte[] body;

		private final GradleFileUploadService gradleFileUploadService;

		private final boolean isKotlinScript;

		private String getBuildGradle() {
			return isKotlinScript ? "build.gradle.kts" : "build.gradle";
		}

		private String getSettingsGradle() {
			return isKotlinScript ? "settings.gradle.kts" : "settings.gradle";
		}

		@Override
		public HelmContext process() {
			ByteArrayInputStream in = new ByteArrayInputStream(body);
			Optional<String> buildGradle = ZipUtil.getZipContent(getBuildGradle(), new ZipInputStream(in));
			in.reset();
			Optional<String> settingsGradle = ZipUtil.getZipContent(getSettingsGradle(), new ZipInputStream(in));

			return buildGradle.map(build -> {

				final String version = GradleUtil.extractVersion(build);

				String settings = settingsGradle.orElseThrow();
				final String name = GradleUtil.extractName(settings);

				return gradleFileUploadService.processBuildFile(build, name, version);
			}).orElseThrow();
		}

		@Override
		public String matchOn() {
			return "type=gradle";
		}

	}

	@RequiredArgsConstructor
	static class MavenBuildProcessor implements BuildProcessor {

		private final byte[] body;

		private final MavenFileUploadService mavenFileUploadService;

		@Override
		public HelmContext process() {
			final String pomXml = readPomFromZip(body).orElseThrow();
			return mavenFileUploadService.processBuildFile(pomXml);
		}

		@Override
		public String matchOn() {
			return "type=maven";
		}

	}

	@SneakyThrows
	private static Optional<String> readPomFromZip(byte[] response) {
		ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(response));
		return ZipUtil.getZipContent("pom.xml", zipInputStream);
	}

	private static void validateLink(String springInitializrLink) {
		if (!springInitializrLink.startsWith("https://start.spring.io/")) {
			throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,
					"Not a spring initializr link");
		}
	}

}
