package com.start.helm.domain.maven.resolvers;

import com.start.helm.domain.helm.HelmChartSlice;
import com.start.helm.domain.helm.HelmContext;
import java.util.List;
import java.util.Optional;

/**
 * Interface defining a DependencyResolver.
 * <p/>
 * A DependencyResolver is responsible for resolving a Maven dependency to a Helm Dependency.
 */
public interface DependencyResolver {

  /**
   * List of Strings against which Maven ArtifactIds are checked with a contains-check.
   */
  List<String> matchOn();

  /**
   * Method for contains-matching a Maven ArtifactId against the matchOn list.
   */
  default boolean matches(String artifactId) {
    return matchOn().stream().anyMatch(artifactId::contains);
  }

  /**
   * Method for providing a {@link HelmChartSlice} through this DependencyResolver.
   * <p/>
   * The {@link Optional} may be empty if the Maven Dependency does not have an equivalent on
   * the infrastructure side (such as "org.postgresql:postgresql" relating to a database). The
   * {@link HelmContext} is still accessible here though to ie set properties.
   * <p/>
   * If the Maven Dependency has an equivalent on the infrastructure side, a {@link HelmChartSlice}
   * must be returned.
   */
  Optional<HelmChartSlice> resolveDependency(HelmContext context);

  /**
   * Name of the dependency.
   */
  String dependencyName();


}
