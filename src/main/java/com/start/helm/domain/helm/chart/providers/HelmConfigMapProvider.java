package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.customizers.TemplateStringPatcher;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class HelmConfigMapProvider implements HelmFileProvider {

  private static final String template = """
      apiVersion: v1
      kind: ConfigMap
      metadata:
        name: {{ include "REPLACEME.fullname" . }}-config
        namespace: {{ .Release.Namespace | quote }}
        labels:
          {{- include "REPLACEME.labels" . | nindent 4 }}
        {{- if .Values.commonAnnotations }}
        annotations: {{- include "common.tplvalues.render" ( dict "value" .Values.commonAnnotations "context" $ ) | nindent 4 }}
        {{- end }}
      data:
        application.properties: |-
          spring.application.name={{ .Values.fullnameOverride }}
      ###@helm-start:configmap
      """;

  @Override
  public String getFileContent(HelmContext context) {
    String filledTemplate = template.replace("REPLACEME", context.getAppName());
    return customize(filledTemplate, context);
  }

  private String customize(String content, HelmContext context) {
    StringBuffer patch = new StringBuffer();
    context.getHelmChartSlices()
        .forEach(f -> f.getDefaultConfig().forEach((k, v) -> patch.append(k).append("=").append(v).append("\n")));
    return cleanup(TemplateStringPatcher.insertAfter(content, marker, patch.toString(), 4));
  }

  private String cleanup(String content) {
    return Arrays.stream(content
            .replace(marker, "")
            .split("\n"))
        .filter(s -> !"".equals(s.trim()))
        .collect(Collectors.joining("\n"));
  }

  private final String marker = "###@helm-start:configmap";

  @Override
  public String getFileName() {
    return "templates/configmap.yaml";
  }

}
