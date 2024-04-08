package me.helmify.domain.helm.bitnami;

import me.helmify.domain.helm.HelmChartTests;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.postgres.quarkus.QsPostgresResolver;
import me.helmify.domain.helm.dependencies.postgres.spring.SgPostgresResolver;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ElasticsearchBitnamiChartTest extends HelmChartTests {

	String bitnami = "bitnami";

	String testSource = "elasticsearch";

	String chartName = "test-elasticsearch-chart";

	@Test
	public void lintBitnamiChartSpringElasticsearch() {
		List<String> starterDependencies = List.of("data-elasticsearch");
		lintAndTestChart(new HelmUnittestContext(chartName, "1.0.0", starterDependencies, testSource, List.of(),
				bitnami, FrameworkVendor.Spring));
	}

	@Test
	public void lintBitnamiChartQuarkusElasticsearch() {
		List<String> starterDependencies = List.of("elasticsearch-rest-client");
		lintAndTestChart(new HelmUnittestContext(chartName, "1.0.0", starterDependencies, testSource, List.of(),
				bitnami, FrameworkVendor.Quarkus));
	}

}
