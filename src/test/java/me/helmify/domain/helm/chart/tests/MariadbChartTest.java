package me.helmify.domain.helm.chart.tests;

import me.helmify.domain.helm.HelmChartTests;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MariadbChartTest extends HelmChartTests {

	@Test
	public void testChartWithMariadb() {

		List<String> starterDependencies = List.of("mariadb");
		//@formatter:off
        List<String> unittestFiles = List.of(
                "deployment_mariadb_test.yaml",
                "service_mariadb_test.yaml",
                "secrets_mariadb_test.yaml",
                "configmap_mariadb_test.yaml"
        );

        lintAndTestChart(
                new HelmUnittestContext(
                        "test-mariadb-chart",
                        "1.0.0",
                        starterDependencies,
                        "mariadb",
                        unittestFiles)
        );
    }

}
