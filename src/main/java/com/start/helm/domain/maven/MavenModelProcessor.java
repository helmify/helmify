package com.start.helm.domain.maven;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.HelmDependency;
import com.start.helm.domain.maven.resolvers.DependencyResolver;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.api.model.Dependency;
import org.apache.maven.api.model.Model;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MavenModelProcessor {

  private final List<DependencyResolver> resolvers;

  public HelmContext process(Model m) {
    List<Dependency> dependencies = m.getDependencies();

    HelmContext context = new HelmContext();

    context.setAppName(m.getArtifactId());
    context.setAppVersion(m.getVersion());

    dependencies.stream()
        .filter(d -> !"test".equals(d.getScope()))
        .map(d -> resolvers
            .stream()
            .filter(matcher -> matcher.matches(d.getArtifactId()))
            .findFirst()
        )
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(d -> d.resolveDependency(context))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet())
        .forEach(f -> {
          Map<String, String> preferredChart = f.getPreferredChart();

          context.addHelmDependency(
              new HelmDependency(preferredChart.get("name"), preferredChart.get("version"), preferredChart.get("repository"),
                  List.of()));
          context.addHelmChartFragment(f);

          Map<String, Object> valuesBlocks = f.getValuesEntries();
          if (valuesBlocks.containsKey("global")) {
            context.addValuesGlobalBlock((Map<String, Object>) valuesBlocks.get("global"));
          }
        });

    log.info("Helm context: {}", context);
    return context;
  }


}
