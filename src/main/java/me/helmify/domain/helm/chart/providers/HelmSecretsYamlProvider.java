package me.helmify.domain.helm.chart.providers;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.chart.TemplateStringPatcher;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;

@Component
public class HelmSecretsYamlProvider implements HelmFileProvider {

	private static final String template = """
			apiVersion: v1
			kind: Secret
			metadata:
			  name: {{ include "REPLACEME.fullname" . }}
			  namespace: {{ .Release.Namespace }}
			  labels:
			    {{- include "REPLACEME.labels" . | nindent 4 }}
			type: Opaque
			data:
			###@helmify:secrets
			""";

	@Override
	public String getFileContent(HelmContext context) {
		String template = HelmSecretsYamlProvider.template.replace("REPLACEME", context.getAppName());
		StringBuffer patch = new StringBuffer();

		context.getHelmChartSlices()
			.stream()
			.filter(f -> f.getSecretEntries() != null)
			.flatMap(f -> f.getSecretEntries().entrySet().stream())
			.forEach(e -> patch.append(e.getKey()).append(": ").append(e.getValue()).append("\n"));

		String patched = TemplateStringPatcher.insertAfter(template, "###@helmify:secrets", patch.toString(), 2);
		return HelmUtil.removeMarkers(patched);
	}

	@Override
	public String getFileName() {
		return "templates/secrets.yaml";
	}

}
