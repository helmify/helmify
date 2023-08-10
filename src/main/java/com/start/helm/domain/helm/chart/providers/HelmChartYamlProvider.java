package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.model.HelmChart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

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
    return yaml.dumpAsMap(HelmChart.builder()
        .apiVersion("v2")
        .name(context.getAppName())
        .version("0.1.0")
        .appVersion(context.getAppVersion())
        .description("A Helm chart for Kubernetes")
        .type(HelmChart.HelmChartType.application)
        .build());
  }

}
