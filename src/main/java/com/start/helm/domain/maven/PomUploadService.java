package com.start.helm.domain.maven;


import static com.start.helm.domain.maven.MavenModelParser.parsePom;

import com.start.helm.domain.helm.HelmContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PomUploadService {

  private final MavenModelProcessor mavenModelProcessor;

  public HelmContext processPom(String pomXml) {
    org.apache.maven.api.model.Model m = parsePom(pomXml).orElseThrow();
    HelmContext helmContext = mavenModelProcessor.process(m);
    helmContext.setAppVersion(m.getVersion());
    helmContext.setAppName(m.getArtifactId());
    helmContext.setDependencyDescriptor(pomXml);
    return helmContext;

  }


}
