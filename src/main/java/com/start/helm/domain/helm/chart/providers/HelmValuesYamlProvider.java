package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.model.HelmValues;
import java.util.HashMap;
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

    mergeValuesGlobalsBlocks(context, buffer);

    buffer.append(yaml.dumpAsMap(
        HelmValues.getDefaultHelmValues(context)
    ));
    buffer.append("\n");

    context.getHelmChartSlices().forEach(f -> f.getValuesEntries().forEach((k, v) -> {
      if (!k.equals("global")) {
        buffer.append(yaml.dumpAsMap(Map.of(k, v)));
        buffer.append("\n");
      }
    }));

    return buffer.toString();
  }

  private void mergeValuesGlobalsBlocks(HelmContext context, StringBuffer buffer) {
    Map<Object, Object> globalMap = new HashMap<>();

    // prepare map
    context.getHelmChartSlices().stream().filter(f -> f.getValuesEntries().containsKey("global"))
        .flatMap(f -> ((Map<String, Object>) f.getValuesEntries().get("global")).keySet().stream())
        .forEach(k -> globalMap.put(k, new HashMap<>()));

    // merge values
    context.getHelmChartSlices().forEach(f -> {
      Map<String, Object> valuesEntries = f.getValuesEntries();
      if (valuesEntries.containsKey("global")) {
        Map<String, Object> block = (Map<String, Object>) valuesEntries.get("global");
        block.forEach((k, v) -> ((Map<String, Object>) globalMap.get(k)).putAll((Map<String, Object>) v));
        System.out.println(globalMap);
      }
    });

    Map<String, Map<Object, Object>> globalBlock = Map.of("global", globalMap);

    buffer.append(yaml.dumpAsMap(globalBlock));
    buffer.append("\n");
  }

  @Override
  public String getFileName() {
    return "values.yaml";
  }
}
