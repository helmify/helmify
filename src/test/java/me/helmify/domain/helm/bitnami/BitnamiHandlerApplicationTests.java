package me.helmify.domain.helm.bitnami;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

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

		Path helmchart = Paths.get("helmchart-" + System.nanoTime());
		Files.createDirectory(helmchart);
		Path templatesDir = Paths.get(helmchart.toFile().getAbsolutePath(), "templates");
		Files.createDirectory(templatesDir);

		for (String key : stringStringMap.keySet()) {

			Path path;
			if (key.endsWith("Chart.yaml") || key.endsWith("values.yaml") || key.endsWith(".helmignore")) {
				path = Paths.get(helmchart.toFile().getAbsolutePath(), key);
			}
			else {
				path = Paths.get(helmchart.toFile().getAbsolutePath(), "templates", key);
			}

			byte[] bytes = stringStringMap.get(key).toString().getBytes();
			System.out.println("Writing " + bytes.length + " bytes to " + path.toFile().getAbsolutePath());
			Files.write(path, bytes);
		}

		System.out.println("Chart files written to " + helmchart.toFile().getAbsolutePath());

	}

}
