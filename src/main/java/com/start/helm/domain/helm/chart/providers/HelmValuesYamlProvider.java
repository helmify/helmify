package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.model.HelmValues;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
@RequiredArgsConstructor
public class HelmValuesYamlProvider implements HelmFileProvider {

  private final Yaml yaml;

  @Override
  public String getFileContent(HelmContext context) {
    return yaml.dumpAsMap(
        HelmValues.getDefaultHelmValues(context)
    );
  }

  @Override
  public String getFileName() {
    return "values.yaml";
  }
}
