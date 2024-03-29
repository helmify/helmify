package me.helmify.domain.helm.chart.providers;

import lombok.RequiredArgsConstructor;
import me.helmify.domain.helm.HelmContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelmHelperProvider implements HelmFileProvider {

	@Override
	public String getFileContent(HelmContext context) {
		return readTemplate("helm/templates/_helpers.tpl").replaceAll("REPLACE_ME", context.getAppName());
	}

	@Override
	public String getFileName() {
		return "templates/_helpers.tpl";
	}

}
