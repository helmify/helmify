package com.start.helm.domain.maven;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.HelmDependency;
import com.start.helm.domain.resolvers.DependencyResolver;
import lombok.RequiredArgsConstructor;
import org.apache.maven.api.model.Dependency;
import org.apache.maven.api.model.Model;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for populating a {@link HelmContext} from a given Maven {@link Model}.
 */
@Service
@RequiredArgsConstructor
public class MavenModelProcessor {

  private final List<DependencyResolver> resolvers;

  /**
   * Here we take in a Maven {@link Model} and inspect its dependencies.
   * <p/>
   * Each non-test dependency is run against a list of {@link DependencyResolver}s.
   * For positive matches, we try to resolve the external dependency (like a database)
   * and add it as a {@link HelmDependency} to the {@link HelmContext}.
   * <p/>
   * We also add a {@link com.start.helm.domain.helm.HelmChartSlice} for each dependency
   * which contains the dependency-specific changes to a Helm Chart, so we can
   * merge those changes later.
   */
  public HelmContext process(Model m) {
    List<Dependency> dependencies = m.getDependencies();

    HelmContext context = new HelmContext();

    context.setAppName(m.getArtifactId());
    context.setAppVersion(m.getVersion());

    dependencies.stream()
        .filter(d -> !"test".equals(d.getScope()))
        .flatMap(d -> resolvers
            .stream()
            .filter(matcher -> matcher.matches(d.getArtifactId()))
            .map(matcher -> matcher.resolveDependency(context))
            .filter(Optional::isPresent)
            .map(Optional::get)
        )
        .collect(Collectors.toSet())
        .forEach(context::addHelmChartFragment);

    return context;
  }


}
