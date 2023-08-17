package com.start.helm.domain.maven;

import java.io.IOException;
import java.util.Optional;
import org.apache.maven.api.model.Model;
import org.apache.maven.model.v4.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.web.multipart.MultipartFile;

public class MavenModelParser {

  public static Optional<Model> parsePom(MultipartFile mavenPom) {
    MavenXpp3Reader reader = new MavenXpp3Reader();
    try {
      return Optional.of(reader.read(mavenPom.getInputStream()));
    } catch (XmlPullParserException | IOException e) {
      return Optional.empty();
    }
  }

}
