package me.helmify.domain.helm.bitnami;

import me.helmify.domain.helm.HelmChartTests;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.postgres.quarkus.QsPostgresResolver;
import me.helmify.domain.helm.dependencies.postgres.spring.SgPostgresResolver;
import org.junit.jupiter.api.Test;

import java.util.List;

public class KafkaBitnamiChartTest extends HelmChartTests {

	String bitnami = "bitnami";

	String testSource = "kafka";

	String chartName = "test-kafka-chart";

	@Test
	public void lintBitnamiChartSpringKafka() {
		List<String> starterDependencies = List.of("kafka");
		lintAndTestChart(new HelmUnittestContext(chartName, "1.0.0", starterDependencies, testSource, List.of(),
				bitnami, FrameworkVendor.Spring));
	}

	@Test
	public void lintBitnamiChartQuarkusKafka() {
		List<String> starterDependencies = List.of("messaging-kafka");
		lintAndTestChart(new HelmUnittestContext(chartName, "1.0.0", starterDependencies, testSource, List.of(),
				bitnami, FrameworkVendor.Quarkus));
	}

}
