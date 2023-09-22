package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.model.HelmChart;
import com.start.helm.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

/**
 * Component for building Chart.yaml
 */
@Component
@RequiredArgsConstructor
public class HelmChartYamlProvider implements HelmFileProvider {

  private final Yaml yaml;

  /**
   * Returns a yaml string containing a helm Chart.yaml
   */
  @Override
  public String getFileContent(HelmContext context) {
    HelmChart chart = HelmChart.builder()
        .apiVersion("v2")
        .name(context.getAppName())
        .version("0.1.0")
        .appVersion(context.getAppVersion())
        .description("A Helm chart for Kubernetes")
        .type(HelmChart.HelmChartType.application)
        .build();
    return customize(chart, context);
  }

  @Override
  public String getFileName() {
    return "Chart.yaml";
  }

  private String customize (HelmChart helmChart, HelmContext context) {

    if (helmChart.getDependencies() == null) {
      helmChart.setDependencies(new ArrayList<>());
    }

    context.getHelmDependencies().forEach(d -> {
      HelmChart.HelmChartDependency helmChartDependency = new HelmChart.HelmChartDependency();
      helmChartDependency.setName(d.name());
      helmChartDependency.setVersion(d.version());
      helmChartDependency.setRepository(d.repository());
      helmChartDependency.setCondition(d.name() + ".enabled");
      helmChartDependency.setTags(Optional.ofNullable(d.tags()).orElse(new ArrayList<>()));
      helmChart.getDependencies().add(helmChartDependency);
    });

    return yaml.dumpAsMap(JsonUtil.fromJson(JsonUtil.toJson(helmChart), Map.class));
  }


}
