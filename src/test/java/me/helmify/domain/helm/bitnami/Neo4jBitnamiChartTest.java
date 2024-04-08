package me.helmify.domain.helm.bitnami;

import me.helmify.domain.helm.HelmChartTests;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.postgres.quarkus.QsPostgresResolver;
import me.helmify.domain.helm.dependencies.postgres.spring.SgPostgresResolver;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Neo4jBitnamiChartTest extends HelmChartTests {

	String bitnami = "bitnami";

	String testSource = "neo4j";

	@Test
	public void lintBitnamiChartSpringNeo4j() {
		List<String> starterDependencies = List.of("data-neo4j");
		lintAndTestChart(new HelmUnittestContext("test-neo4j-chart", "1.0.0", starterDependencies, testSource,
				List.of(), bitnami, FrameworkVendor.Spring));
	}

	@Test
	public void lintBitnamiChartQuarkusNeo4j() {
		List<String> starterDependencies = List.of("quarkus-neo4j");
		lintAndTestChart(new HelmUnittestContext("test-neo4j-chart", "1.0.0", starterDependencies, testSource,
				List.of(), bitnami, FrameworkVendor.Quarkus));
	}

}
