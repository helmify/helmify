package me.helmify.domain.helm.chart.providers;

import lombok.RequiredArgsConstructor;
import me.helmify.app.config.YamlConfig;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.HelmChartSlice;
import me.helmify.domain.helm.HelmContext;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.List;

import static me.helmify.domain.helm.chart.TemplateStringPatcher.insertAfter;
import static me.helmify.domain.helm.chart.TemplateStringPatcher.removeBetween;

@Component
@RequiredArgsConstructor
public class HelmDeploymentYamlProvider implements HelmFileProvider {

	@Override
	public String getFileContent(HelmContext context) {

		String template = readTemplate("helm/templates/deployment.yaml").replaceAll("REPLACE_ME", context.getAppName());

		return customize(template, context);
	}

	@Override
	public String getFileName() {
		return "templates/deployment.yaml";
	}

	private String customize(String content, HelmContext context) {
		String withInitContainers = injectInitContainers(content, context);

		// remove http probes if it's not a webapp
		if (!context.isCreateIngress()) {
			withInitContainers = removeBetween("###@helmify:probes", "###@helmify:lifecycle", withInitContainers);
		}

		// if it's a webapp and we have an actuator..
		if (context.isCreateIngress() && context.isHasActuator()) {
			// remove default probe
			withInitContainers = removeBetween("###@helmify:probes", "###@helmify:lifecycle", withInitContainers);

			FrameworkVendor vendor = context.getFrameworkVendor();
			String liveness = vendor.equals(FrameworkVendor.Spring) ? "/actuator/health/liveness"
					: vendor.equals(FrameworkVendor.Quarkus) ? "/q/health/live" : "";
			String readiness = vendor.equals(FrameworkVendor.Spring) ? "/actuator/health/readiness"
					: vendor.equals(FrameworkVendor.Quarkus) ? "/q/health/ready" : "";

			withInitContainers = insertAfter(withInitContainers, "###@helmify:probes", String.format("""
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

		return HelmUtil.removeMarkers(injectEnvVars(withInitContainers, context));
	}

	private static String injectInitContainers(String content, HelmContext context) {
		StringBuffer buffer = new StringBuffer();
		Yaml yaml = YamlConfig.getInstance();
		context.getHelmChartSlices()
			.stream()
			.filter(f -> f.getInitContainer() != null)
			.forEach(slice -> buffer.append(yaml.dump(List.of(slice.getInitContainer()))).append("\n"));
		String withInitContainer = insertAfter(content, "###@helmify:initcontainers", buffer.toString(), 6);
		return insertAfter(withInitContainer, "###@helmify:initcontainers", "initContainers:\n", 6).replace("'\"", "\"")
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
			String withEnvVars = insertAfter(content, "###@helmify:envblock", buffer.toString(), 10);
			return insertAfter(withEnvVars, "###@helmify:envblock", "env:\n", 10).replace("'{{", "{{")
				.replace("}}'", "}}");
		}

		return content;
	}

}
