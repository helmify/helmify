package com.start.helm.domain.helm.chart.customizers;


import com.start.helm.app.config.YamlConfig;
import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.model.HelmChart;
import java.util.ArrayList;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;

@RequiredArgsConstructor
public class ChartYamlCustomizer implements Function<String, String> {

  @Override
  public String apply(String chartYaml) {
    HelmChart helmChart = yaml.loadAs(chartYaml, HelmChart.class);
    if (helmChart.getDependencies() == null) {
      helmChart.setDependencies(new ArrayList<>());
    }

    helmContext.getHelmDependencies().forEach(d -> {
      HelmChart.HelmChartDependency helmChartDependency = new HelmChart.HelmChartDependency();
      helmChartDependency.setName(d.name());
      helmChartDependency.setVersion(d.version());
      helmChartDependency.setRepository(d.repository());
      helmChartDependency.setCondition(d.name() + ".enabled");
      helmChartDependency.setTags(d.tags());
      helmChart.getDependencies().add(helmChartDependency);
    });

    return yaml.dumpAsMap(helmChart);
  }

  private final Yaml yaml = new YamlConfig().yaml();
  private final HelmContext helmContext;

}
