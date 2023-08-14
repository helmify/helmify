package com.start.helm.domain.maven.resolvers;

import com.start.helm.domain.helm.HelmChartFragment;
import com.start.helm.domain.helm.HelmContext;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class PostgresDependencyResolver implements DependencyResolver {

  @Override
  public List<String> matchOn() {
    return List.of("postgres");
  }

  @Override
  public Optional<HelmChartFragment> resolveDependency(HelmContext context) {
    HelmChartFragment fragment = new HelmChartFragment();

    //    TODO
    //    fragment.setEnvironmentEntries(List.of());
    //    fragment.setDefaultConfig(List.of());
    //    fragment.setPreferredChart(List.of());
    //    fragment.setValuesEntries(List.of());
    //    fragment.setSecretEntries(List.of());
    //    fragment.setInitContainer(List.of());


    return Optional.of(fragment);
  }

  @Override
  public String dependencyName() {
    return "postgres";
  }
}
