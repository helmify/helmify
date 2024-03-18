package me.helmify.domain.helm.chart.tests;

import org.junit.jupiter.api.Test;

import java.util.List;

public class Neo4jChartTest extends HelmChartTests {

	@Test
	public void testChartWithNeo4j() {

		List<String> starterDependencies = List.of("data-neo4j");
		//@formatter:off
        List<String> unittestFiles = List.of(
                "deployment_neo4j_test.yaml",
                "service_neo4j_test.yaml",
                "secrets_neo4j_test.yaml",
                "configmap_neo4j_test.yaml"
        );

        lintAndTestChart(
                new HelmUnittestContext(
                        "test-neo4j-chart",
                        "1.0.0",
                        starterDependencies,
                        "neo4j",
                        unittestFiles)
        );
    }

}
