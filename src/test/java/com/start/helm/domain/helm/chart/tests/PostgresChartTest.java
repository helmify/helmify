package com.start.helm.domain.helm.chart.tests;

import org.junit.jupiter.api.Test;

import java.util.List;

public class PostgresChartTest extends HelmChartTests {

	@Test
	public void testChartWithPostgres() {

		List<String> starterDependencies = List.of("postgresql");
		//@formatter:off
        List<String> unittestFiles = List.of(
                "deployment_postgres_test.yaml",
                "service_postgres_test.yaml",
                "configmap_postgres_test.yaml",
                "secrets_postgres_test.yaml"
        );

        lintAndTestChart(
                new HelmUnittestContext(
                        "test-postgres-chart",
                        "1.0.0",
                        starterDependencies,
                        "postgres",
                        unittestFiles)
        );
    }

}
