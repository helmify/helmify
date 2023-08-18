package com.start.helm.domain.maven;

import static com.start.helm.domain.maven.MavenModelParser.parsePom;

import com.start.helm.domain.helm.HelmContext;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class PomUploadController {

  private final MavenModelProcessor mavenModelProcessor;

  @PostMapping("/upload-pom")
  public String uploadImage(Model viewModel, @RequestParam("pom") MultipartFile mavenPom) throws IOException {
    int length = mavenPom.getBytes().length;
    viewModel.addAttribute("message", "Uploaded pom.xml of length " + length + " bytes");

    Optional<org.apache.maven.api.model.Model> model = parsePom(mavenPom);
    model.ifPresentOrElse(
        m -> {
          HelmContext helmContext = mavenModelProcessor.process(m);
          helmContext.setAppVersion(m.getVersion());
          helmContext.setAppName(m.getArtifactId());

          setDescriptor(mavenPom, helmContext);

          viewModel.addAttribute("helmContext", helmContext);
        },
        () -> viewModel.addAttribute("error", "Could not parse pom.xml"));

    return "fragments :: pom-upload-form";
  }

  @SneakyThrows
  private static void setDescriptor(MultipartFile mavenPom, HelmContext helmContext) {
    helmContext.setDependencyDescriptor(new String(mavenPom.getBytes()));
  }


}
