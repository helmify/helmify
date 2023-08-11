package com.start.helm.domain.helm.chart.providers;

import static com.start.helm.domain.helm.chart.customizers.TemplateStringPatcher.insertAfter;

import com.start.helm.domain.helm.HelmContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelmDeploymentYamlProvider implements HelmFileProvider {

  private static final String template = """
      apiVersion: apps/v1
      kind: Deployment
      metadata:
        name: {{ include "%s.fullname" . }}
        labels:
          {{- include "%s.labels" . | nindent 4 }}
      spec:
        {{- if not .Values.autoscaling.enabled }}
        replicas: {{ .Values.replicaCount }}
        {{- end }}
        selector:
          matchLabels:
            {{- include "%s.selectorLabels" . | nindent 6 }}
        template:
          metadata:
            {{- with .Values.podAnnotations }}
            annotations:
              {{- toYaml . | nindent 8 }}
            {{- end }}
            labels:
              {{- include "%s.selectorLabels" . | nindent 8 }}
          spec:
            {{- with .Values.imagePullSecrets }}
            imagePullSecrets:
              {{- toYaml . | nindent 8 }}
            {{- end }}
            serviceAccountName: {{ include "%s.serviceAccountName" . }}
            securityContext:
              {{- toYaml .Values.podSecurityContext | nindent 8 }}
      ###@helm-start:initcontainers
            containers:
              - name: {{ .Chart.Name }}
                securityContext:
                  {{- toYaml .Values.securityContext | nindent 12 }}
                image: "{{ .Values.image.repository }}:{{ .Values.image.tag | default .Chart.AppVersion }}"
                imagePullPolicy: {{ .Values.image.pullPolicy }}
      ###@helm-start:envblock
                ports:
                  - name: http
                    containerPort: {{ .Values.service.port }}
                    protocol: TCP
                livenessProbe:
                  httpGet:
                    path: /
                    port: http
                readinessProbe:
                  httpGet:
                    path: /
                    port: http
                resources:
                  {{- toYaml .Values.resources | nindent 12 }}
            {{- with .Values.nodeSelector }}
            nodeSelector:
              {{- toYaml . | nindent 8 }}
            {{- end }}
            {{- with .Values.affinity }}
            affinity:
              {{- toYaml . | nindent 8 }}
            {{- end }}
            {{- with .Values.tolerations }}
            tolerations:
              {{- toYaml . | nindent 8 }}
            {{- end }}
        """;

  @Override
  public String getFileContent(HelmContext context) {
    return String.format(template, context.getAppName(), context.getAppName(), context.getAppName(),
        context.getAppName(), context.getAppName());
  }

}
