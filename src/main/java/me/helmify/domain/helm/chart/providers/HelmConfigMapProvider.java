package me.helmify.domain.helm.chart.providers;

import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.chart.TemplateStringPatcher;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

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

	@Override
	public String patchContent(String content, HelmContext context) {
		return HelmUtil.removeMarkers(customize(content, context));
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
			String chartFlavor = context.getChartFlavor();
			String portExpression = "bitnami".equals(chartFlavor) ? ".Values.service.ports.healthcheck"
					: ".Values.healthcheck.port";

			if (vendor.equals(FrameworkVendor.Spring)) {
				patch.append("management.server.port={{ %s }}\n".formatted(portExpression));
				patch.append("management.endpoints.web.exposure.include=\"*\"\n".formatted(portExpression));
			}

			if (vendor.equals(FrameworkVendor.Quarkus)) {
				patch.append("quarkus.management.enabled=true\n");
				patch.append("quarkus.management.port={{ %s }}\n".formatted(portExpression));
			}

		}

		// set server port
		if (context.isCreateIngress()) {

			String chartFlavor = context.getChartFlavor();
			String portExpression = "bitnami".equals(chartFlavor) ? ".Values.service.ports.http"
					: ".Vales.service.port";

			if (vendor.equals(FrameworkVendor.Spring)) {

				patch.append("server.port={{ %s }}\n".formatted(portExpression));
			}

			if (vendor.equals(FrameworkVendor.Quarkus)) {
				patch.append("quarkus.http.port={{ %s }}\n".formatted(portExpression));
			}
		}

		if (vendor.equals(FrameworkVendor.Quarkus)) {
			patch.append("\nquarkus.log.level=DEBUG\n")
				.append("quarkus.log.min-level=DEBUG\n")
				.append("quarkus.log.console.enable=true\n")
				.append("quarkus.log.console.format=%d{HH:mm:ss} %-5p [%c] %s%e%n\n");
		}

		String string = patch.toString();
		String filled = HelmUtil
			.removeMarkers(TemplateStringPatcher.insertAfter(content, "###@helmify:configmap", string, 4));

		if ("bitnami".equals(context.getChartFlavor())) {
			filled = filled.lines().map(line -> {
				if (line.contains("  application-prod.properties: |"))
					return "";
				// convert property notation to env var notation
				if (line.contains(".") && line.contains("=")) {
					String[] split = line.split("=");
					return "  " + (split[0].toUpperCase().replaceAll("\\.", "_") + ": \"" + split[1] + "\"")
						.replaceAll("\"\"", "\"")
						.trim();
				}
				return line;
			}).filter(line -> !line.trim().isEmpty()).collect(Collectors.joining("\r\n"));
		}

		return filled;
	}

	@Override
	public String getFileName() {
		return "templates/configmap.yaml";
	}

}
