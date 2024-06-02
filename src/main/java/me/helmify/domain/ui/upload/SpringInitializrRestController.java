package me.helmify.domain.ui.upload;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.build.gradle.GradleFileUploadService;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.build.maven.MavenFileUploadService;
import me.helmify.util.GradleUtil;
import me.helmify.util.ZipUtil;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${spring.initializr.host}")
	private String initializrHost;

	private String getInitializrHost() {
		return String.format("https://%s/", this.initializrHost);
	}

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

		final String zipLink = springInitializrLink.replace(getInitializrHost() + "#!",
				getInitializrHost() + "starter.zip?");

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

		return "fragments :: second-form";
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

	private void validateLink(String springInitializrLink) {
		if (!springInitializrLink.startsWith(getInitializrHost())) {
			throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,
					"Not a spring initializr link");
		}
	}

}
