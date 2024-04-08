package me.helmify.domain.helm.chart.tests;

import me.helmify.domain.helm.HelmChartTests;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MySqlChartTest extends HelmChartTests {

	@Test
	public void testChartWithMySql() {

		List<String> starterDependencies = List.of("mysql");
		//@formatter:off
        List<String> unittestFiles = List.of(
                "deployment_mysql_test.yaml",
                "service_mysql_test.yaml",
                "secrets_mysql_test.yaml",
                "configmap_mysql_test.yaml"
        );

        lintAndTestChart(
                new HelmUnittestContext(
                        "test-mysql-chart",
                        "1.0.0",
                        starterDependencies,
                        "mysql",
                        unittestFiles)
        );
    }

}
