package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.util.HelmUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.start.helm.domain.helm.chart.customizers.TemplateStringPatcher.insertAfter;

@Component
@RequiredArgsConstructor
public class HelmServiceYamlProvider implements HelmFileProvider {

  private static final String template = """
          apiVersion: v1
          kind: Service
          metadata:
            name: {{ include "%s.fullname" . }}
              labels:
                {{- include "%s.labels" . | nindent 4 }}
            spec:
              type: {{ .Values.service.type }}
              ports:
                - port: {{ .Values.service.port }}
                  targetPort: http
                  protocol: TCP
                  name: http
          ###@helm-start:healthcheckport
              selector:
                {{- include "%s.selectorLabels" . | nindent 4 }}
              """;

  private static final String healthCheckPortPatch = """
          - port: {{ .Values.healthcheck.port }}
            targetPort: {{ .Values.healthcheck.port }}
            protocol: TCP
            name: healthcheck
          """;

  @Override
  public String getFileContent(HelmContext context) {
    String formatted = String.format(template, context.getAppName(), context.getAppName(), context.getAppName());
    if (context.isHasActuator()) {
      formatted = addHealthCheckPort(formatted);
    }
    return HelmUtil.removeMarkers(formatted);
  }

  private String addHealthCheckPort(String content) {
    return insertAfter(content, "###@helm-start:healthcheckport", healthCheckPortPatch, 8);
  }

  @Override
  public String getFileName() {
    return "templates/service.yaml";
  }

}
