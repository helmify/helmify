package me.helmify.domain.helm.bitnami;

import me.helmify.domain.helm.HelmChartTests;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.postgres.quarkus.QsPostgresResolver;
import me.helmify.domain.helm.dependencies.postgres.spring.SgPostgresResolver;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CassandraBitnamiChartTest extends HelmChartTests {

	String bitnami = "bitnami";

	String testSource = "cassandra";

	String chartName = "test-cassandra-chart";

	@Test
	public void lintBitnamiChartSpringCassandra() {
		List<String> starterDependencies = List.of("data-cassandra");
		lintAndTestChart(new HelmUnittestContext(chartName, "1.0.0", starterDependencies, testSource, List.of(),
				bitnami, FrameworkVendor.Spring));
	}

	public void lintBitnamiChartQuarkusCassandra() {
		List<String> starterDependencies = List.of("cassandra-quarkus-client");
		lintAndTestChart(new HelmUnittestContext(chartName, "1.0.0", starterDependencies, testSource, List.of(),
				bitnami, FrameworkVendor.Quarkus));
	}

}
