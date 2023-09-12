package com.start.helm.domain.maven.resolvers;

import com.start.helm.domain.helm.HelmChartSlice;
import com.start.helm.domain.helm.HelmContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Resolver for spring actuator dependency.
 */
@Component
public class SpringBootStarterActuatorResolver implements DependencyResolver {

  @Override
  public String dependencyName() {
    return "actuator";
  }

  @Override
  public List<String> matchOn() {
    return List.of("actuator");
  }

  @Override
  public Optional<HelmChartSlice> resolveDependency(HelmContext context) {
    context.setHasActuator(true);
    HelmChartSlice slice = new HelmChartSlice();
    slice.setValuesEntries(Map.of("healthcheck", Map.of(
            "port", 8090,
            "name", "healthcheck"
    )));
    return Optional.of(slice);
  }
}
