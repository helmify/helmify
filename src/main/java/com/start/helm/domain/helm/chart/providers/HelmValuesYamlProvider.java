package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmChartFragment;
import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.model.HelmValues;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
@RequiredArgsConstructor
public class HelmValuesYamlProvider implements HelmFileProvider {

  private final Yaml yaml;

  @Override
  public String getFileContent(HelmContext context) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(yaml.dumpAsMap(
        HelmValues.getDefaultHelmValues(context)
    ));
    buffer.append("\n");
    context.getHelmChartFragments().stream().map(HelmChartFragment::getValuesEntries).forEach(entry -> {
      buffer.append(yaml.dump(entry));
      buffer.append("\n");
    });

    return buffer.toString();
  }

  @Override
  public String getFileName() {
    return "values.yaml";
  }
}
