package me.helmify.domain.helm.chart.providers;

import lombok.RequiredArgsConstructor;
import me.helmify.domain.helm.HelmContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelmNotesProvider implements HelmFileProvider {

	@Override
	public String getFileContent(HelmContext context) {
		return readTemplate("helm/templates/NOTES.txt").replace("REPLACE_ME", context.getAppName());
	}

	@Override
	public String getFileName() {
		return "templates/NOTES.txt";
	}

}
