package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.customizers.TemplateStringPatcher;
import org.springframework.stereotype.Component;

@Component
public class HelmSecretsYamlProvider implements HelmFileProvider{

  private static final String template = """
      apiVersion: v1
      kind: Secret
      metadata:
        name: {{ include "REPLACEME.fullname" . }}
        namespace: {{ .Release.Namespace }}
        labels:
          {{- include "REPLACEME.labels" . | nindent 4 }}
      type: Opaque
      data:
      ###@helm-start:secrets
      """;

  @Override
  public String getFileContent(HelmContext context) {
    String template = HelmSecretsYamlProvider.template.replace("REPLACEME", context.getAppName());
    StringBuffer patch = new StringBuffer();

    context
            .getHelmChartSlices()
            .stream()
            .filter(f -> f.getSecretEntries() != null)
        .flatMap(f -> f.getSecretEntries().entrySet().stream())
        .forEach(e -> patch.append(e.getKey()).append(": ").append(e.getValue()).append("\n"));

    return TemplateStringPatcher.insertAfter(template, "###@helm-start:secrets", patch.toString(), 2);
  }

  @Override
  public String getFileName() {
    return "templates/secrets.yaml";
  }
}
