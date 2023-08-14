package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmChartFragment;
import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.customizers.TemplateStringPatcher;
import java.util.Set;
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
    return customize(filledTemplate, context.getHelmChartFragments());
  }

  private String customize(String content, Set<HelmChartFragment> fragments) {

    StringBuffer patch = new StringBuffer();


    fragments.forEach(f -> {
      f.getDefaultConfig().forEach((k, v) -> {
        patch.append(k).append("=").append(v).append("\n");
      });
    });

    return TemplateStringPatcher.insertAfter(content, "###@helm-start:configmap", patch.toString(), 4);
  }

}
