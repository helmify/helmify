package com.start.helm.domain.helm.chart.providers;

import com.start.helm.app.config.YamlConfig;
import com.start.helm.domain.FrameworkVendor;
import com.start.helm.domain.helm.HelmChartSlice;
import com.start.helm.domain.helm.HelmContext;
import com.start.helm.util.HelmUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.List;

import static com.start.helm.domain.helm.chart.customizers.TemplateStringPatcher.insertAfter;
import static com.start.helm.domain.helm.chart.customizers.TemplateStringPatcher.removeBetween;

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
			              mountPath: ###@helm-start:configpath/application.properties
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
		boolean isSpring = context.getFrameworkVendor().equals(FrameworkVendor.Spring);
		boolean isQuarkus = context.getFrameworkVendor().equals(FrameworkVendor.Quarkus);

		String configPath = isSpring ? "/workspace/BOOT-INF/classes" : isQuarkus ? "/deployments/config" : "";

		String filledTemplate = template.replace("REPLACEME", context.getAppName())
			.replace("###@helm-start:configpath", configPath);
		return HelmUtil.removeMarkers(customize(filledTemplate, context));
	}

	@Override
	public String getFileName() {
		return "templates/deployment.yaml";
	}

	private String customize(String content, HelmContext context) {
		String withInitContainers = injectInitContainers(content, context);

		// remove http probes if it's not a webapp
		if (!context.isCreateIngress()) {
			withInitContainers = removeBetween("###@helm-start:probes", "###@helm-start:lifecycle", withInitContainers);
		}

		// if it's a webapp and we have an actuator..
		if (context.isCreateIngress() && context.isHasActuator()) {
			// remove default probe
			withInitContainers = removeBetween("###@helm-start:probes", "###@helm-start:lifecycle", withInitContainers);

			FrameworkVendor vendor = context.getFrameworkVendor();
			String liveness = vendor.equals(FrameworkVendor.Spring) ? "/actuator/health/liveness"
					: vendor.equals(FrameworkVendor.Quarkus) ? "/q/health/live" : "";
			String readiness = vendor.equals(FrameworkVendor.Spring) ? "/actuator/health/readiness"
					: vendor.equals(FrameworkVendor.Quarkus) ? "/q/health/ready" : "";

			withInitContainers = insertAfter(withInitContainers, "###@helm-start:probes", String.format("""
					  - name: healthcheck
					    containerPort: {{ .Values.healthcheck.port }}
					    protocol: TCP
					livenessProbe:
					  initialDelaySeconds: 10
					  periodSeconds: 10
					  successThreshold: 1
					  timeoutSeconds: 5
					  failureThreshold: 3
					  httpGet:
					    path: %s
					    port: healthcheck
					readinessProbe:
					  initialDelaySeconds: 10
					  periodSeconds: 10
					  successThreshold: 1
					  timeoutSeconds: 5
					  failureThreshold: 3
					  httpGet:
					    path: %s
					    port: healthcheck
					""", liveness, readiness), 10);
		}

		return injectEnvVars(withInitContainers, context);
	}

	private static String injectInitContainers(String content, HelmContext context) {
		StringBuffer buffer = new StringBuffer();
		Yaml yaml = YamlConfig.getInstance();
		context.getHelmChartSlices()
			.stream()
			.filter(f -> f.getInitContainer() != null)
			.forEach(slice -> buffer.append(yaml.dump(List.of(slice.getInitContainer()))).append("\n"));
		String withInitContainer = insertAfter(content, "###@helm-start:initcontainers", buffer.toString(), 6);
		return insertAfter(withInitContainer, "###@helm-start:initcontainers", "initContainers:\n", 6)
			.replace("'\"", "\"")
			.replace("\"'", "\"");
	}

	private static String injectEnvVars(String content, HelmContext context) {
		StringBuffer buffer = new StringBuffer();
		Yaml yaml = YamlConfig.getInstance();
		List<HelmChartSlice> slicesWithEnvEntries = context.getHelmChartSlices()
			.stream()
			.filter(f -> f.getEnvironmentEntries() != null && !f.getEnvironmentEntries().isEmpty())
			.toList();

		if (!slicesWithEnvEntries.isEmpty()) {
			slicesWithEnvEntries.forEach(slice -> buffer.append(yaml.dump(slice.getEnvironmentEntries())).append("\n"));
			String withEnvVars = insertAfter(content, "###@helm-start:envblock", buffer.toString(), 10);
			return insertAfter(withEnvVars, "###@helm-start:envblock", "env:\n", 10).replace("'{{", "{{")
				.replace("}}'", "}}");
		}

		return content;
	}

}
