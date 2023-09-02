package com.start.helm.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.zip.ZipEntry;
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
  private final UploadService uploadService;

  @Getter
  @Setter
  @ToString
  public static class SpringInitializrRequest {
    private String springInitializrLink;
  }

  @PostMapping("/spring-initializr-link")
  public String generate(Model viewModel, @RequestBody SpringInitializrRequest request) {
    log.info("Generate request: {}", request);

    final String springInitializrLink = request.getSpringInitializrLink();
    validateLink(springInitializrLink);

    final String zipLink = springInitializrLink.replace("https://start.spring.io/#!", "https://start.spring.io/starter.zip?");

    ResponseEntity<byte[]> response = restTemplate.getForEntity(zipLink, byte[].class);
    log.info("Received Response, Code {}", response.getStatusCode());

    final String pomXml = readPomFromZip(response).orElseThrow();

    viewModel.addAttribute("springInitializrLink", springInitializrLink);

    return uploadService.processPom(viewModel, pomXml);
  }

  @SneakyThrows
  private static Optional<String> readPomFromZip(ResponseEntity<byte[]> response) {
    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      byte[] body = response.getBody();
      log.info("Received body of {} bytes", body.length);

      ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(body));
      ZipEntry entry;

      while ((entry = zipInputStream.getNextEntry()) != null) {
        String filename = entry.getName();
        if ("pom.xml".equals(filename)) {
          byte[] buffer = new byte[10240];
          int len;

          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

          while ((len = zipInputStream.read(buffer)) > 0) {
            byteArrayOutputStream.write(buffer, 0, len);
          }

          final String pomXml = byteArrayOutputStream.toString(StandardCharsets.UTF_8);
          log.debug("pom.xml: {}", pomXml);
          return Optional.of(pomXml);
        }
      }
    }
    return Optional.empty();
  }

  private static void validateLink(String springInitializrLink) {
    if (!springInitializrLink.startsWith("https://start.spring.io/")) {
      throw new ResponseStatusException(
          org.springframework.http.HttpStatus.BAD_REQUEST, "Not a spring initializr link"
      );
    }

    if (!springInitializrLink.contains("type=maven-project")) {
      throw new ResponseStatusException(
          org.springframework.http.HttpStatus.BAD_REQUEST, "Not a maven project"
      );
    }
  }

}
