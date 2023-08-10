package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelmServiceAccountYamlProvider implements HelmFileProvider{

  private static final String template = """
      {{- if .Values.serviceAccount.create -}}
      apiVersion: v1
      kind: ServiceAccount
      metadata:
        name: {{ include "%s.serviceAccountName" . }}
        labels:
          {{- include "%s.labels" . | nindent 4 }}
        {{- with .Values.serviceAccount.annotations }}
        annotations:
          {{- toYaml . | nindent 4 }}
        {{- end }}
      {{- end }}
      """;

  @Override
  public String getFileContent(HelmContext context) {
    return String.format(template, context.getAppName(), context.getAppName());
  }

}
