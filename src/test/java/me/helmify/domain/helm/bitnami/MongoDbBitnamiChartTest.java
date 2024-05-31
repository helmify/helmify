package me.helmify.domain.helm.bitnami;

import me.helmify.domain.helm.HelmChartTests;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.postgres.quarkus.QsPostgresResolver;
import me.helmify.domain.helm.dependencies.postgres.spring.SgPostgresResolver;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MongoDbBitnamiChartTest extends HelmChartTests {

	String bitnami = "bitnami";

	String testSource = "mongodb";

	@Test
	public void lintBitnamiChartSpringMongodb() {
		List<String> starterDependencies = List.of("data-mongodb");
		lintAndTestChart(new HelmUnittestContext("test-mongodb-chart", "1.0.0", starterDependencies, testSource,
				List.of(), bitnami, FrameworkVendor.Spring));
	}

	@Test
	public void lintBitnamiChartQuarkusMongodb() {
		List<String> starterDependencies = List.of("mongodb-client");
		lintAndTestChart(new HelmUnittestContext("test-mongodb-chart", "1.0.0", starterDependencies, testSource,
				List.of(), bitnami, FrameworkVendor.Quarkus));
	}

}
