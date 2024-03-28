package me.helmify.domain.helm.chart.providers;

import lombok.RequiredArgsConstructor;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.chart.TemplateStringPatcher;
import me.helmify.domain.helm.model.HelmValues;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

@Component
@RequiredArgsConstructor
public class HelmValuesYamlProvider implements HelmFileProvider {

	private final Yaml yaml;

	@Override
	public String getFileContent(HelmContext context) {
		StringBuffer buffer = new StringBuffer();

		mergeValuesGlobalsBlocks(context, buffer);

		HelmValues defaultHelmValues = HelmValues.getDefaultHelmValues(context);

		if (Optional.ofNullable(context.getCustomized()).orElse(false)) {
			HelmContext.HelmContextCustomization customizations = context.getCustomizations();

			List<String> secrets = new ArrayList<>();
			if (customizations.getDockerImagePullSecret() != null
					&& !"".equals(customizations.getDockerImagePullSecret().trim())) {
				secrets.add(customizations.getDockerImagePullSecret());
			}

			defaultHelmValues.setImage(new HelmValues.HelmValuesImage(customizations.getDockerImageRepositoryUrl(),
					customizations.getDockerImageTag(), HelmValues.HelmValuesImage.ImagePullPolicy.IfNotPresent,
					secrets));
		}

		buffer.append(yaml.dumpAsMap(defaultHelmValues));
		buffer.append("\n");

		context.getHelmChartSlices().forEach(f -> f.getValuesEntries().forEach((k, v) -> {
			if (!k.equals("global")) {
				buffer.append(yaml.dumpAsMap(Map.of(k, v)));
				buffer.append("\n");
			}
		}));

		return buffer.toString();
	}

	@Override
	public String patchContent(String content, HelmContext context) {
		StringBuffer stringBuffer = new StringBuffer(content);
		mergeValuesGlobalsBlocks(context, stringBuffer);
		return stringBuffer.toString();
	}

	private void mergeValuesGlobalsBlocks(HelmContext context, StringBuffer buffer) {
		Map<Object, Object> globalMap = new HashMap<>();

		// prepare map
		context.getHelmChartSlices()
			.stream()
			.filter(f -> f.getValuesEntries().containsKey("global"))
			.flatMap(f -> ((Map<String, Object>) f.getValuesEntries().get("global")).keySet().stream())
			.forEach(k -> globalMap.put(k, new HashMap<>()));

		// merge values
		context.getHelmChartSlices().forEach(f -> {
			Map<String, Object> valuesEntries = f.getValuesEntries();
			if (valuesEntries.containsKey("global")) {
				Map<String, Object> block = (Map<String, Object>) valuesEntries.get("global");
				block.forEach((k, v) -> ((Map<String, Object>) globalMap.get(k)).putAll((Map<String, Object>) v));
			}
		});

		Map<String, Map<Object, Object>> globalBlock = Map.of("global", globalMap);
		String newGlobalBlock = yaml.dumpAsMap(globalBlock);

		if (buffer.indexOf(globalsMarker) != -1) {

			String globalsAsString = yaml.dumpAsMap(globalMap);
			String patched = TemplateStringPatcher.insertAfter(buffer.toString(), globalsMarker, globalsAsString, 2);
			buffer.setLength(0);
			buffer.append(patched);
			return;
		}

		buffer.append(newGlobalBlock);
		buffer.append("\n");
	}

	String globalsMarker = "## @helmify:globals";

	@Override
	public String getFileName() {
		return "values.yaml";
	}

}
