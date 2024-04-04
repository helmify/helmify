package me.helmify.domain.helm.chart.providers;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.chart.TemplateStringPatcher;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class HelmConfigMapProvider implements HelmFileProvider {

	@Override
	public String getFileContent(HelmContext context) {
		String template = readTemplate("helm/templates/configmap.yaml").replaceAll("REPLACE_ME", context.getAppName());
		return HelmUtil.removeMarkers(customize(template, context));
	}

	@Override
	public String patchContent(String content, HelmContext context) {
		return HelmUtil.removeMarkers(customize(content, context));
	}

	private String customize(String content, HelmContext context) {
		content = content.replaceAll("%%COMPONENT_NAME%%", context.getAppName());
		StringBuffer patch = new StringBuffer();
		context.getHelmChartSlices()
			.stream()
			.filter(f -> f.getDefaultConfig() != null)
			.forEach(f -> f.getDefaultConfig().forEach((k, v) -> patch.append(k).append("=").append(v).append("\n")));

		FrameworkVendor vendor = context.getFrameworkVendor();
		String chartFlavor = context.getChartFlavor();
		boolean hasActuator = context.isHasActuator();
		boolean createIngress = context.isCreateIngress();

		String healthCheckPortExpression = "bitnami".equals(chartFlavor) ? ".Values.service.ports.healthcheck"
				: ".Values.healthcheck.port";
		String exposureInclude = "bitnami".equals(chartFlavor) ? "\"*\"" : "*";
		String portExpression = "bitnami".equals(chartFlavor) ? ".Values.service.ports.http" : ".Values.service.port";

		switch (vendor) {
			case Spring -> {
				patch.append("SPRING_PROFILES_ACTIVE={{ .Values.spring.profiles.active }}\n");
				patch.append("SPRING_APPLICATION_NAME={{ .Values.fullnameOverride }}\n");
				if (hasActuator) {
					patch.append("MANAGEMENT_SERVER_PORT={{ %s }}\n".formatted(healthCheckPortExpression));
					patch.append("MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=%s\n".formatted(exposureInclude));
				}
				if (createIngress)
					patch.append("SERVER_PORT={{ %s }}\n".formatted(portExpression));
			}
			case Quarkus -> {
				patch.append("QUARKUS_APPLICATION_NAME={{ .Values.fullnameOverride }}\n");
				patch.append("QUARKUS_LOG_LEVEL=DEBUG\n");
				patch.append("QUARKUS_LOG_MIN-LEVEL=DEBUG\n");
				patch.append("QUARKUS_LOG_CONSOLE_ENABLE=true\n");
				patch.append("QUARKUS_LOG_CONSOLE_FORMAT=%d{HH:mm:ss} %-5p [%c] %s%e%n\n");
				if (hasActuator) {
					patch.append("QUARKUS_MANAGEMENT_ENABLED=true\n");
					patch.append("QUARKUS_MANAGEMENT_PORT={{ %s }}\n".formatted(healthCheckPortExpression));
				}
				if (createIngress)
					patch.append("QUARKUS_HTTP_PORT={{ %s }}\n".formatted(portExpression));
			}
		}

		String populatedConfigMap = TemplateStringPatcher.insertAfter(content, "###@helmify:configmap",
				patch.toString(), 2);
		return HelmUtil.removeMarkers(populatedConfigMap)
			.lines()
			.filter(l -> !l.contains("REMOVE:"))
			.filter(line -> !line.trim().isEmpty())
			.collect(Collectors.joining("\r\n"));
	}

	@Override
	public String getFileName() {
		return "templates/configmap.yaml";
	}

}
