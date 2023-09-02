package com.start.helm.domain.maven;

import java.io.IOException;
import java.util.Optional;
import org.apache.maven.api.model.Model;
import org.apache.maven.model.v4.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Class for parsing a Maven {@link Model}.
 */
public class MavenModelParser {

  /**
   * Method for parsing a Maven {@link Model} from a {@link MultipartFile}.
   * <p/>
   * If the file provided is a valid pom.xml, {@link Optional}'s isPresent() will be true,
   * false otherwise.
   */
  public static Optional<Model> parsePom(String mavenPom) {
    MavenXpp3Reader reader = new MavenXpp3Reader();
    try {
      return Optional.of(reader.read(new java.io.StringReader(mavenPom)));
    } catch (XmlPullParserException | IOException e) {
      return Optional.empty();
    }
  }

}
