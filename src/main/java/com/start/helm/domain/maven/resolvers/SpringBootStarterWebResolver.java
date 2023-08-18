package com.start.helm.domain.maven.resolvers;

import com.start.helm.domain.helm.HelmChartSlice;
import com.start.helm.domain.helm.HelmContext;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Resolver for spring web dependencies.
 */
@Component
public class SpringBootStarterWebResolver implements DependencyResolver {

  @Override
  public String dependencyName() {
    return "web";
  }

  @Override
  public List<String> matchOn() {
    return List.of("spring-boot-starter-web", "spring-boot-starter-webflux", "spring-boot-starter-graphql");
  }

  @Override
  public Optional<HelmChartSlice> resolveDependency(HelmContext context) {
    context.setCreateIngress(true);
    return Optional.empty();
  }
}
