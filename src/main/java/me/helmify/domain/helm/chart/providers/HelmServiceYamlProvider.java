package me.helmify.domain.helm.chart.providers;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.helmify.domain.helm.HelmContext;
import me.helmify.util.HelmUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import static me.helmify.domain.helm.chart.TemplateStringPatcher.insertAfter;

@Component
@RequiredArgsConstructor
public class HelmServiceYamlProvider implements HelmFileProvider {

	private static final String healthCheckPortPatch = """
			- port: {{ .Values.healthcheck.port }}
			  targetPort: {{ .Values.healthcheck.port }}
			  protocol: TCP
			  name: healthcheck
			      """;

	@Override
	public String patchContent(String content, HelmContext context) {
		String formatted = content.replace("REPLACE_ME", context.getAppName());
		if (context.isHasActuator()) {
			formatted = addHealthCheckPort(formatted);
		}
		return HelmUtil.removeMarkers(formatted);
	}

	@SneakyThrows
	@Override
	public String getFileContent(HelmContext context) {
		String content = readTemplate("helm/templates/service.yaml");
		return patchContent(content, context);
	}

	private String addHealthCheckPort(String content) {
		return insertAfter(content, "###@helmify:healthcheckport", healthCheckPortPatch, 4);
	}

	@Override
	public String getFileName() {
		return "templates/service.yaml";
	}

}
