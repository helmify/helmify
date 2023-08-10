package com.start.helm.domain.maven;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.HelmChartService;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.maven.model.v4.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class PomUploadController {

  private final MavenModelProcessor mavenModelProcessor;
  private final HelmChartService helmChartService;

  @PostMapping("/upload-pom") public String uploadImage(Model viewModel, @RequestParam("pom") MultipartFile mavenPom) throws IOException {
    int length = mavenPom.getBytes().length;
    viewModel.addAttribute("message", "Uploaded pom.xml of length " + length + " bytes");

    Optional<org.apache.maven.api.model.Model> model = parsePom(mavenPom);
    model.ifPresentOrElse(
        m -> {
          HelmContext helmContext = mavenModelProcessor.process(m);
          helmContext.setAppVersion(m.getVersion());
          helmContext.setAppName(m.getArtifactId());

          helmChartService.process(helmContext);

          viewModel.addAttribute("dependencies", helmContext);
        },
        () -> viewModel.addAttribute("error", "Could not parse pom.xml"));

    return "fragments :: pom-upload-form";
  }

  private Optional<org.apache.maven.api.model.Model> parsePom(MultipartFile mavenPom) {
    MavenXpp3Reader reader = new MavenXpp3Reader();
    try {
      return Optional.of(reader.read(mavenPom.getInputStream()));
    } catch (XmlPullParserException | IOException e) {
      return Optional.empty();
    }
  }

}