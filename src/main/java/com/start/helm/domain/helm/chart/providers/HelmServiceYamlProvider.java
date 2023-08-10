package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmContext;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
@RequiredArgsConstructor
public class HelmServiceYamlProvider implements HelmFileProvider{

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
        selector:
          {{- include "%s.selectorLabels" . | nindent 4 }}
      """;

  @Override
  public String getFileContent(HelmContext context) {
    return String.format(template, context.getAppName(), context.getAppName(), context.getAppName());
  }

}