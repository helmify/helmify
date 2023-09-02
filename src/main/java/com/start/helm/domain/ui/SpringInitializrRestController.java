package com.start.helm.domain.ui;

import com.start.helm.domain.gradle.GradleUploadService;
import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.maven.PomUploadService;
import com.start.helm.util.GradleUtil;
import com.start.helm.util.ZipUtil;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SpringInitializrRestController {

  private final RestTemplate restTemplate;
  private final PomUploadService pomUploadService;
  private final GradleUploadService gradleUploadService;

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

    final String zipLink = springInitializrLink.replace("https://start.spring.io/#!", "https://start.spring.io/starter.zip?");

    ResponseEntity<byte[]> response = restTemplate.getForEntity(zipLink, byte[].class);
    log.info("Received Response, Code {}", response.getStatusCode());
    validateResponseCode(response);

    boolean isUsingKotlinScript = springInitializrLink.contains("gradle-project-kotlin");

    byte[] body = response.getBody();

    HelmContext helmContext =
        Stream.of(new GradleBuildProcessor(body, gradleUploadService, isUsingKotlinScript),
                new MavenBuildProcessor(body, pomUploadService))
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
      throw new ResponseStatusException(
          org.springframework.http.HttpStatus.BAD_REQUEST, "Could not download zip"
      );
    }
  }

  interface BuildProcessor {
    HelmContext process();

    String matchOn();
  }

  @RequiredArgsConstructor
  static
  class GradleBuildProcessor implements BuildProcessor {
    private final byte[] body;
    private final GradleUploadService gradleUploadService;
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

        return gradleUploadService.processGradleBuild(build, name, version);
      }).orElseThrow();
    }

    @Override
    public String matchOn() {
      return "type=gradle";
    }
  }

  @RequiredArgsConstructor
  static
  class MavenBuildProcessor implements BuildProcessor {

    private final byte[] body;
    private final PomUploadService pomUploadService;

    @Override
    public HelmContext process() {
      final String pomXml = readPomFromZip(body).orElseThrow();
      return pomUploadService.processPom(pomXml);
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
      throw new ResponseStatusException(
          org.springframework.http.HttpStatus.BAD_REQUEST, "Not a spring initializr link"
      );
    }
  }

}
