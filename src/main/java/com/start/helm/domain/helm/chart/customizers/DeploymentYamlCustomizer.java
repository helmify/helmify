package com.start.helm.domain.helm.chart.customizers;

import static com.start.helm.domain.helm.chart.customizers.TemplateStringPatcher.insertAfter;

import com.start.helm.app.config.YamlConfig;
import com.start.helm.domain.helm.HelmContext;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class DeploymentYamlCustomizer implements TemplateCustomizer {

  private static final String envBlockBreakpoint = "###@helm-start:envblock";
  // for demo purposes, this will be built dynamically later.
  private String envPatch = """
      env:
        - name: TEST
          value: "test"
      """;

  @Override
  public String customize(String template, HelmContext context) {
    Map<String, List<Object>> env = Map.of("env", List.of(Map.of("name", "TEST", "value", "test")));
    String patch = YamlConfig.getInstance().dumpAsMap(env);
    return insertAfter(template, envBlockBreakpoint, patch, 10);
  }
}
