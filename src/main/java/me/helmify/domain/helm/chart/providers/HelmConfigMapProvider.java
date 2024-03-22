package me.helmify.domain.helm.chart.providers;

import me.helmify.domain.helm.resolvers.FrameworkVendor;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.chart.TemplateStringPatcher;
import me.helmify.util.HelmUtil;
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
			###@helmify:configmap
			""";

	@Override
	public String getFileContent(HelmContext context) {
		String filledTemplate = template.replace("REPLACEME", context.getAppName());
		return HelmUtil.removeMarkers(customize(filledTemplate, context));
	}

	private String customize(String content, HelmContext context) {
		StringBuffer patch = new StringBuffer();
		context.getHelmChartSlices()
			.stream()
			.filter(f -> f.getDefaultConfig() != null)
			.forEach(f -> f.getDefaultConfig().forEach((k, v) -> patch.append(k).append("=").append(v).append("\n")));

		FrameworkVendor vendor = context.getFrameworkVendor();
		if (vendor.equals(FrameworkVendor.Spring)) {
			patch.append("spring.application.name={{ .Values.fullnameOverride }}\n");
		}

		if (vendor.equals(FrameworkVendor.Quarkus)) {
			patch.append("quarkus.application.name={{ .Values.fullnameOverride }}\n");
		}

		// set separate port for actuator, we don't want to expose actuator through an
		// ingress
		if (context.isHasActuator()) {
			if (vendor.equals(FrameworkVendor.Spring)) {
				patch.append("management.server.port={{ .Values.healthcheck.port }}\n");
			}

			if (vendor.equals(FrameworkVendor.Quarkus)) {
				patch.append("quarkus.management.enabled=true\n");
				patch.append("quarkus.management.port={{ .Values.healthcheck.port }}\n");
			}

		}

		// set server port
		if (context.isCreateIngress()) {
			if (vendor.equals(FrameworkVendor.Spring)) {
				patch.append("server.port={{ .Values.service.port }}\n");
			}

			if (vendor.equals(FrameworkVendor.Quarkus)) {
				patch.append("quarkus.http.port={{ .Values.service.port }}\n");
			}
		}

		if (vendor.equals(FrameworkVendor.Quarkus)) {
			patch.append("\nquarkus.log.level=DEBUG\n")
				.append("quarkus.log.min-level=DEBUG\n")
				.append("quarkus.log.console.enable=true\n")
				.append("quarkus.log.console.format=%d{HH:mm:ss} %-5p [%c] %s%e%n\n");
		}

		return HelmUtil
			.removeMarkers(TemplateStringPatcher.insertAfter(content, "###@helmify:configmap", patch.toString(), 4));
	}

	@Override
	public String getFileName() {
		return "templates/configmap.yaml";
	}

}
