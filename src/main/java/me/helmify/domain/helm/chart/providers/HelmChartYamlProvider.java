package me.helmify.domain.helm.chart.providers;

import lombok.RequiredArgsConstructor;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.chart.model.HelmChart;
import me.helmify.util.JsonUtil;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Component for building Chart.yaml
 */
@Component
@RequiredArgsConstructor
public class HelmChartYamlProvider implements HelmFileProvider {

	private final Yaml yaml;

	/**
	 * Returns a yaml string containing a helm Chart.yaml
	 */
	@Override
	public String getFileContent(HelmContext context) {
		HelmChart chart = HelmChart.builder()
			.apiVersion("v2")
			.name(context.getAppName())
			.version("0.1.0")
			.appVersion(context.getAppVersion())
			.description("A Helm chart for Kubernetes")
			.type(HelmChart.HelmChartType.application)
			.build();
		return customize(chart, context);
	}

	@Override
	public String getFileName() {
		return "Chart.yaml";
	}

	private static HelmChart.HelmChartDependency getBitnamiCommonDependency() {
		HelmChart.HelmChartDependency helmChartDependency = new HelmChart.HelmChartDependency();
		helmChartDependency.setName("common");
		helmChartDependency.setVersion("2.x.x");
		helmChartDependency.setRepository("oci://registry-1.docker.io/bitnamicharts");
		helmChartDependency.setTags(List.of("bitnami-common"));
		helmChartDependency.setCondition("true");
		return helmChartDependency;
	}

	private String customize(HelmChart helmChart, HelmContext context) {

		if (helmChart.getDependencies() == null) {
			helmChart.setDependencies(new ArrayList<>());
		}

		context.getHelmDependencies().forEach(d -> {
			HelmChart.HelmChartDependency helmChartDependency = new HelmChart.HelmChartDependency();
			helmChartDependency.setName(d.name());
			helmChartDependency.setVersion(d.version());
			helmChartDependency.setRepository(d.repository());
			helmChartDependency.setCondition(d.name() + ".enabled");
			helmChartDependency.setTags(Optional.ofNullable(d.tags()).orElse(new ArrayList<>()));
			helmChart.getDependencies().add(helmChartDependency);
		});

		helmChart.getDependencies().add(getBitnamiCommonDependency());

		return yaml.dumpAsMap(JsonUtil.fromJson(JsonUtil.toJson(helmChart), Map.class));
	}

}
