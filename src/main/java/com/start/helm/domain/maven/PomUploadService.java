package com.start.helm.domain.maven;


import static com.start.helm.domain.maven.MavenModelParser.parsePom;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.maven.MavenModelProcessor;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Slf4j
@Service
@RequiredArgsConstructor
public class PomUploadService {

  private final MavenModelProcessor mavenModelProcessor;

  public String processPom(Model viewModel, String pomXml) {
    int length = pomXml.length();
    viewModel.addAttribute("message", "Uploaded pom.xml of length " + length + " bytes");

    Optional<org.apache.maven.api.model.Model> model = parsePom(pomXml);
    model.ifPresentOrElse(
        m -> {
          HelmContext helmContext = mavenModelProcessor.process(m);
          helmContext.setAppVersion(m.getVersion());
          helmContext.setAppName(m.getArtifactId());
          helmContext.setDependencyDescriptor(pomXml);

          viewModel.addAttribute("helmContext", helmContext);
        },
        () -> viewModel.addAttribute("error", "Could not parse pom.xml"));

    return "fragments :: pom-upload-form";
  }


}
