package com.start.helm.domain.helm.chart.providers;

import static com.start.helm.domain.helm.chart.customizers.TemplateStringPatcher.insertAfter;

import com.start.helm.app.config.YamlConfig;
import com.start.helm.domain.helm.HelmContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
@RequiredArgsConstructor
public class HelmDeploymentYamlProvider implements HelmFileProvider {

  private static final String template = """
      apiVersion: apps/v1
      kind: Deployment
      metadata:
        name: {{ include "REPLACEME.fullname" . }}
        labels:
          {{- include "REPLACEME.labels" . | nindent 4 }}
      spec:
        {{- if not .Values.autoscaling.enabled }}
        replicas: {{ .Values.replicaCount }}
        {{- end }}
        selector:
          matchLabels:
            {{- include "REPLACEME.selectorLabels" . | nindent 6 }}
        template:
          metadata:
            {{- with .Values.podAnnotations }}
            annotations:
              {{- toYaml . | nindent 8 }}
            {{- end }}
            labels:
              {{- include "REPLACEME.selectorLabels" . | nindent 8 }}
          spec:
            {{- with .Values.imagePullSecrets }}
            imagePullSecrets:
              {{- toYaml . | nindent 8 }}
            {{- end }}
            serviceAccountName: {{ include "REPLACEME.serviceAccountName" . }}
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
                # allow for graceful shutdown
                lifecycle:
                  preStop:
                    exec:
                      command: ["sh", "-c", "sleep 10"]
                volumeMounts:
                  - name: {{ include "REPLACEME.fullname" . }}-config-vol
                    mountPath: /config/application.properties
                    subPath: application.properties
                resources:
                  {{- toYaml .Values.resources | nindent 12 }}
            volumes:
              - name: {{ include "REPLACEME.fullname" . }}-config-vol
                projected:
                  sources:
                    - configMap:
                        name: {{ include "REPLACEME.fullname" . }}-config
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
    String filledTemplate = template.replace("REPLACEME", context.getAppName());
    return customize(filledTemplate, context);
  }

  @Override
  public String getFileName() {
    return "templates/deployment.yaml";
  }

  private String customize(String content, HelmContext context) {
    final String withInitContainers = injectInitContainers(content, context);
    return injectEnvVars(withInitContainers, context);
  }

  private static String injectInitContainers(String content, HelmContext context) {
    StringBuffer buffer = new StringBuffer();
    Yaml yaml = YamlConfig.getInstance();
    context.getHelmChartFragments().forEach(f -> buffer.append(yaml.dump(List.of(f.getInitContainer()))).append("\n"));
    String withInitContainer = insertAfter(content, "###@helm-start:initcontainers", buffer.toString(), 6);
    return insertAfter(withInitContainer, "###@helm-start:initcontainers", "initContainers:\n", 6)
        .replace("'\"", "\"")
        .replace("\"'", "\"");
  }

  private static String injectEnvVars(String content, HelmContext context) {
    StringBuffer buffer = new StringBuffer();
    Yaml yaml = YamlConfig.getInstance();
    context.getHelmChartFragments().forEach(f -> buffer.append(yaml.dump(f.getEnvironmentEntries())).append("\n"));
    String withEnvVars = insertAfter(content, "###@helm-start:envblock", buffer.toString(), 10);
    return insertAfter(withEnvVars, "###@helm-start:envblock", "env:\n", 10)
        .replace("'{{", "{{")
        .replace("}}'", "}}");
  }

}
