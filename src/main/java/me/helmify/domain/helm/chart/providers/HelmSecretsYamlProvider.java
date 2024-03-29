package me.helmify.domain.helm.chart.providers;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.chart.TemplateStringPatcher;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;

@Component
public class HelmSecretsYamlProvider implements HelmFileProvider {

	@Override
	public String patchContent(String content, HelmContext context) {
		StringBuffer patch = new StringBuffer();

		context.getHelmChartSlices()
			.stream()
			.filter(f -> f.getSecretEntries() != null)
			.flatMap(f -> f.getSecretEntries().entrySet().stream())
			.forEach(e -> patch.append(e.getKey()).append(": ").append(e.getValue()).append("\n"));

		String patched = TemplateStringPatcher.insertAfter(content, "###@helmify:secrets", patch.toString(), 2);
		return HelmUtil.removeMarkers(patched);
	}

	@Override
	public String getFileContent(HelmContext context) {
		String template = readTemplate("helm/templates/secrets.yaml").replaceAll("REPLACE_ME", context.getAppName());
		return patchContent(template, context);
	}

	@Override
	public String getFileName() {
		return "templates/secrets.yaml";
	}

}
