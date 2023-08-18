package com.start.helm.domain.maven.resolvers;

import com.start.helm.domain.helm.HelmChartSlice;
import com.start.helm.domain.helm.HelmContext;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SpringBootStarterActuatorResolver implements DependencyResolver {

  @Override
  public String dependencyName() {
    return "web";
  }

  @Override
  public List<String> matchOn() {
    return List.of("actuator");
  }

  @Override
  public Optional<HelmChartSlice> resolveDependency(HelmContext context) {
    context.setHasActuator(true);
    return Optional.empty();
  }
}
