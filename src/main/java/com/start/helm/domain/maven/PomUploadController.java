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

/**
 * Controller for handling upload of a pom.xml file.
 * <p/>
 * This is the first controller to be called by the client upon a user action.
 * The user is expected to upload a Maven pom.xml file. We parse this file
 * and try to populate a {@link HelmContext} from that.
 */
@Controller
@RequiredArgsConstructor
public class PomUploadController {

  private final MavenModelProcessor mavenModelProcessor;

  /**
   * Method which handles a file upload from the client.
   * <p/>
   * We expect a request param called "pom" which must be a multipart file.
   * The provided pom is parsed and a {@link HelmContext} is populated from it
   * which is then put into the View Model: {@link Model} to expose to the client.
   */
  @PostMapping("/upload-pom")
  public String uploadPom(Model viewModel, @RequestParam("pom") MultipartFile mavenPom) throws IOException {
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
