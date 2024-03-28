package me.helmify.domain.helm.bitnami;

import me.helmify.domain.helm.chart.providers.HelmConfigMapProvider;
import me.helmify.domain.helm.chart.providers.HelmValuesYamlProvider;
import me.helmify.util.HelmUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BitnamiChart {

	private static final List<String> replaceables = List.of("%%MAIN_OBJECT_BLOCK%%", "%%MAIN_CONTAINER_NAME%%",
			"%%IMAGE_NAME%%", "%%IMAGE_TAG%%", "%%CHART_NAME%%", "%%CONTAINER_NAME%%", "%%COMPONENT_NAME%%",
			"%%TEMPLATE_NAME%%", "%%MAIN_CONTAINER%%", "%%CONFIG_FILE_NAME%%");

	private final Map<String, String> chartFiles = new HashMap<>();

	private List<Resource> getChartFiles() {
		try {

			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resolver.getResources("classpath:bitnami/**/*");
			return Arrays.asList(resources);
		}
		catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public void load() {
		if (chartFiles.isEmpty()) {
			List<Resource> files = getChartFiles();
			files.forEach(r -> {
				String name = r.getFilename();
				if (name != null && !name.endsWith("templates")) {
					String key = (isHelmRootFile(name) ? name : "templates/" + name);
					String value = getResourceContent(r);
					chartFiles.put(key, value);
				}
			});
		}
	}

	public Map<String, String> populateBitnamiChart(BitnamiChartContext context) {
		Map<String, String> result = new HashMap<>();
		chartFiles.forEach((k, v) -> {
			String content = v;
			for (String replaceable : replaceables) {
				content = switch (replaceable) {
					case "%%MAIN_CONTAINER%%" ->
						content.replaceAll(replaceable, context.getOriginalContext().getAppName());
					case "%%TEMPLATE_NAME%%", "%%CHART_NAME%%" ->
						content.replaceAll(replaceable, context.getChartName());
					case "%%MAIN_OBJECT_BLOCK%%" -> content.replaceAll(replaceable, context.getMainObjectBlock());
					case "%%MAIN_CONTAINER_NAME%%", "%%CONTAINER_NAME%%" ->
						content.replaceAll(replaceable, context.getMainObjectBlock().toLowerCase());
					case "%%IMAGE_NAME%%" -> content.replaceAll(replaceable, context.getImageName());
					case "%%IMAGE_TAG%%" -> content.replaceAll(replaceable, context.getImageTag());
					case "%%COMPONENT_NAME%%" -> content.replaceAll(replaceable, context.getComponentName());
					case "%%CONFIG_FILE_NAME%%" -> content.replaceAll(replaceable, context.getConfigFileName());
					default -> content;
				};
			}
			result.put(k, content);
		});

		postProcessFiles(result, context);
		return result;
	}

	private void postProcessFiles(Map<String, String> files, BitnamiChartContext context) {
		postProcessChartsYaml(files, context);
		postProcessValuesYaml(files, context);
		postProcessConfigMap(files, context);

	}

	private static void postProcessConfigMap(Map<String, String> files, BitnamiChartContext context) {
		String configMapFile = "templates/configmap.yaml";
		String configMap = files.get(configMapFile);
		String patchedConfigMap = new HelmConfigMapProvider().patchContent(configMap, context.getOriginalContext());
		files.put(configMapFile, patchedConfigMap);
	}

	private static void postProcessValuesYaml(Map<String, String> files, BitnamiChartContext context) {
		String chartYaml = files.get("values.yaml");
		String clean = chartYaml.lines()
			.filter(line -> !line.contains("%%"))
			.filter(line -> !line.trim().equals("##"))
			.filter(line -> !line.trim().isEmpty())
			.collect(Collectors.joining("\r\n"));
		clean = clean.replaceAll("## @section", "\r\n## @section");
		clean = new HelmValuesYamlProvider(new Yaml()).patchContent(clean, context.getOriginalContext());
		files.put("values.yaml", clean);
	}

	private static void postProcessChartsYaml(Map<String, String> files, BitnamiChartContext context) {
		String chartYaml = files.get("Chart.yaml");
		Yaml yaml = new Yaml();
		Map map = yaml.loadAs(chartYaml, Map.class);
		map.put("appVersion", context.getOriginalContext().getAppVersion());
		map.put("description", "Bitnami Helm Chart for " + context.getOriginalContext().getAppName());
		map.put("name", context.getChartName());
		files.put("Chart.yaml", yaml.dump(map));
	}

	private String getResourceContent(Resource resource) {
		try {
			return resource.getContentAsString(StandardCharsets.UTF_8);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isHelmRootFile(String filename) {
		return filename.endsWith("Chart.yaml") || filename.endsWith("values.yaml") || filename.endsWith(".helmignore")
				|| filename.endsWith("README.md");
	}

}
