package com.start.helm.domain.maven.resolvers;

import com.start.helm.domain.helm.HelmContext;
import java.util.List;

public interface DependencyResoler {
    List<String> matchOn();

    default boolean matches(String artifactId) {
      return matchOn().stream().anyMatch(artifactId::equals);
    }

    void updateHelmContext(HelmContext context);

    default String lookupString() {
      return "";
    }

  }
