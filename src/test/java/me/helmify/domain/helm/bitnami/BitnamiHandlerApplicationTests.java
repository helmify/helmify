package me.helmify.domain.helm.bitnami;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@SpringBootTest
class BitnamiHandlerApplicationTests {

	@Test
	void contextLoads() throws Exception {
		BitnamiChart bitnamiChart = new BitnamiChart();
		bitnamiChart.load();

		BitnamiChartContext context = new BitnamiChartContext();
		context.setChartName("test-chart");
		context.setComponentName("test-component-name");
		context.setImageName("image-name");
		context.setImageTag("image-tag");
		context.setConfigFileName("config-file-name");
		context.setMainObjectBlock("mainObjectBlock");

		Map<String, String> stringStringMap = bitnamiChart.populateBitnamiChart(context);
		stringStringMap.forEach((k, v) -> {
			System.out.println("File: " + k);
			System.out.println(v);
		});
	}

	public class BitnamiChart {

		private static final List<String> replaceables = List.of("%%MAIN_OBJECT_BLOCK%%", "%%MAIN_CONTAINER_NAME%%",
				"%%IMAGE_NAME%%", "%%IMAGE_TAG%%", "%%CHART_NAME%%", "%%CONTAINER_NAME%%", "%%COMPONENT_NAME%%",
				"%%CONFIG_FILE_NAME%%");

		private final Map<String, String> chartFiles = new HashMap<>();

		private List<File> getChartFiles() {
			try {
				return Arrays.asList(new ClassPathResource("bitnami").getFile().listFiles());
			}
			catch (IOException e) {
				e.printStackTrace();
				return new ArrayList<>();
			}
		}

		private String readFile(Path p) {
			try {
				if (!p.toFile().isFile())
					return "";
				return Files.readString(p);
			}
			catch (IOException e) {
				e.printStackTrace();
				return "";
			}
		}

		public void load() {
			if (chartFiles.isEmpty()) {
				List<File> bitnami = getChartFiles();
				bitnami.forEach(f -> chartFiles.put(f.getName(), readFile(f.toPath())));
			}
		}

		public Map<String, String> populateBitnamiChart(BitnamiChartContext context) {
			Map<String, String> result = new HashMap<>();
			chartFiles.forEach((k, v) -> {
				String content = v;
				for (String replaceable : replaceables) {
					content = switch (replaceable) {
						case "%%MAIN_OBJECT_BLOCK%%" -> content.replace(replaceable, context.getMainObjectBlock());
						case "%%MAIN_CONTAINER_NAME%%" ->
							content.replace(replaceable, context.getMainObjectBlock().toLowerCase());
						case "%%IMAGE_NAME%%" -> content.replace(replaceable, context.getImageName());
						case "%%IMAGE_TAG%%" -> content.replace(replaceable, context.getImageTag());
						case "%%CHART_NAME%%" -> content.replace(replaceable, context.getChartName());
						case "%%CONTAINER_NAME%%" ->
							content.replace(replaceable, context.getMainObjectBlock().toLowerCase());
						case "%%COMPONENT_NAME%%" -> content.replace(replaceable, context.getComponentName());
						case "%%CONFIG_FILE_NAME%%" -> content.replace(replaceable, context.getConfigFileName());
						default -> content;
					};
				}
				result.put(k, content);
			});

			return result;
		}

	}

	public class BitnamiChartContext {

		String chartName;

		// camelCased appName
		String mainObjectBlock;

		String componentName;

		String imageName;

		String imageTag;

		String configFileName;

		public String getChartName() {
			return chartName;
		}

		public void setChartName(String chartName) {
			this.chartName = chartName;
		}

		public String getMainObjectBlock() {
			return mainObjectBlock;
		}

		public void setMainObjectBlock(String mainObjectBlock) {
			this.mainObjectBlock = mainObjectBlock;
		}

		public String getComponentName() {
			return componentName;
		}

		public void setComponentName(String componentName) {
			this.componentName = componentName;
		}

		public String getImageName() {
			return imageName;
		}

		public void setImageName(String imageName) {
			this.imageName = imageName;
		}

		public String getImageTag() {
			return imageTag;
		}

		public void setImageTag(String imageTag) {
			this.imageTag = imageTag;
		}

		public String getConfigFileName() {
			return configFileName;
		}

		public void setConfigFileName(String configFileName) {
			this.configFileName = configFileName;
		}

	}

}
