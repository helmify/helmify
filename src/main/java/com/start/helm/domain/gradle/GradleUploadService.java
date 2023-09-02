package com.start.helm.domain.gradle;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.maven.resolvers.DependencyResolver;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradleUploadService {

  private final List<DependencyResolver> resolvers;

  /**
   * Method which accepts a build.gradle file as String and builds a {@link HelmContext} from it.
   * <p/>
   * A note on gradle handling:
   * After looking into gradle's tooling api, this simpler approach has proven as effective
   * in extracting the few bits of data we need to populate a {@link HelmContext}.
   */
  public HelmContext processGradleBuild(String buildFile, String appName, String appVersion) {
    log.info("Processing Gradle Build: {}", buildFile);

    HelmContext context = new HelmContext();

    context.setAppName(appName);
    context.setAppVersion(appVersion);
    context.setDependencyDescriptor(buildFile);

    buildFile
        .lines()
        .map(String::trim)
        .filter(line -> line.startsWith("implementation"))
        .map(line -> line.replace("implementation", ""))
        .map(line -> line.replace("'", ""))
        .map(line -> line.replace("\"", ""))
        .map(line -> line.replace("(", "").replace(")", ""))
        .map(line -> line.split(":")[1])
        .flatMap(artifactId -> resolvers
            .stream()
            .filter(matcher -> matcher.matches(artifactId))
            .map(matcher -> matcher.resolveDependency(context))
            .filter(Optional::isPresent)
            .map(Optional::get)
        )
        .collect(Collectors.toSet())
        .forEach(context::addHelmChartFragment);

    return context;
  }

}
