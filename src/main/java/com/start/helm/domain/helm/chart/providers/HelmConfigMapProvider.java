package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.customizers.TemplateStringPatcher;
import com.start.helm.util.HelmUtil;
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
    return HelmUtil.removeMarkers(customize(filledTemplate, context));
  }

  private String customize(String content, HelmContext context) {
    StringBuffer patch = new StringBuffer();
    context.getHelmChartSlices()
            .stream().filter(f -> f.getDefaultConfig() != null)
            .forEach(f -> f.getDefaultConfig().forEach((k, v) -> patch.append(k).append("=").append(v).append("\n")));
    // set separate port for actuator, we don't want to expose actuator through an ingress
    if (context.isHasActuator()) {
      patch.append("management.server.port={{ .Values.healthcheck.port }}\n");
    }

    // set server port
    if (context.isCreateIngress()) {
      patch.append("server.port={{ .Values.service.port }}\n");
    }

    return HelmUtil.removeMarkers(TemplateStringPatcher.insertAfter(content, "###@helm-start:configmap", patch.toString(), 4));
  }

  @Override
  public String getFileName() {
    return "templates/configmap.yaml";
  }

}
