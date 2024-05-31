package me.helmify.domain.helm.chart.providers;

import lombok.RequiredArgsConstructor;
import me.helmify.domain.helm.HelmContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelmServiceAccountYamlProvider implements HelmFileProvider {

	@Override
	public String getFileContent(HelmContext context) {
		String content = readTemplate("helm/templates/serviceaccount.yaml");
		return content.replaceAll("REPLACE_ME", context.getAppName());
	}

	@Override
	public String getFileName() {
		return "templates/serviceaccount.yaml";
	}

}
