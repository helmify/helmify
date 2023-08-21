package com.start.helm.domain.helm.chart.providers;

import static com.start.helm.domain.helm.chart.customizers.TemplateStringPatcher.insertAfter;
import static com.start.helm.domain.helm.chart.customizers.TemplateStringPatcher.removeBetween;

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
      ###@helm-start:probes
                livenessProbe:
                  httpGet:
                    path: /
                    port: http
                readinessProbe:
                  httpGet:
                    path: /
                    port: http
      ###@helm-start:lifecycle
                # allow for graceful shutdown
                lifecycle:
                  preStop:
                    exec:
                      command: ["sh", "-c", "sleep 10"]
                volumeMounts:
                  - name: {{ include "REPLACEME.fullname" . }}-config-vol
                    mountPath: /workspace/BOOT-INF/classes/application.properties
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
    String withInitContainers = injectInitContainers(content, context);
    if (!context.isCreateIngress() || !context.isHasActuator()) {
      // remove probes
      withInitContainers = removeBetween("###@helm-start:probes", "###@helm-start:lifecycle", withInitContainers);
    }

    if (context.isCreateIngress() && context.isHasActuator()) {
      // set probe paths
      withInitContainers = removeBetween("###@helm-start:probes", "###@helm-start:lifecycle", withInitContainers);
      withInitContainers = insertAfter(withInitContainers, "###@helm-start:probes", """
          livenessProbe:
            initialDelaySeconds: 10
            periodSeconds: 10
            successThreshold: 1
            timeoutSeconds: 5
            failureThreshold: 3
            httpGet:
              path: /actuator/health/liveness
              port: http
          readinessProbe:
            initialDelaySeconds: 10
            periodSeconds: 10
            successThreshold: 1
            timeoutSeconds: 5
            failureThreshold: 3
            httpGet:
              path: /actuator/health/readiness
              port: http
          """, 10);
    }

    return injectEnvVars(withInitContainers, context);
  }

  private static String injectInitContainers(String content, HelmContext context) {
    StringBuffer buffer = new StringBuffer();
    Yaml yaml = YamlConfig.getInstance();
    context.getHelmChartSlices().forEach(f -> buffer.append(yaml.dump(List.of(f.getInitContainer()))).append("\n"));
    String withInitContainer = insertAfter(content, "###@helm-start:initcontainers", buffer.toString(), 6);
    return insertAfter(withInitContainer, "###@helm-start:initcontainers", "initContainers:\n", 6)
        .replace("'\"", "\"")
        .replace("\"'", "\"");
  }

  private static String injectEnvVars(String content, HelmContext context) {
    StringBuffer buffer = new StringBuffer();
    Yaml yaml = YamlConfig.getInstance();
    context.getHelmChartSlices().forEach(f -> buffer.append(yaml.dump(f.getEnvironmentEntries())).append("\n"));
    String withEnvVars = insertAfter(content, "###@helm-start:envblock", buffer.toString(), 10);
    return insertAfter(withEnvVars, "###@helm-start:envblock", "env:\n", 10)
        .replace("'{{", "{{")
        .replace("}}'", "}}");
  }

}
