package com.start.helm.domain.maven.resolvers;

import com.start.helm.domain.helm.HelmChartFragment;
import com.start.helm.domain.helm.HelmContext;
import java.util.List;
import java.util.Optional;

public interface DependencyResolver {

    List<String> matchOn();

    default boolean matches(String artifactId) {
      return matchOn().stream().anyMatch(artifactId::equals);
    }

    Optional<HelmChartFragment> resolveDependency(HelmContext context);

    String dependencyName();

  }
