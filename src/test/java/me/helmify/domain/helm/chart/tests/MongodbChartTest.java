package me.helmify.domain.helm.chart.tests;

import me.helmify.domain.helm.HelmChartTests;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MongodbChartTest extends HelmChartTests {

	@Test
	public void testChartWithMongodb() {

		List<String> starterDependencies = List.of("data-mongodb");
		//@formatter:off
        List<String> unittestFiles = List.of(
                "deployment_mongodb_test.yaml",
                "service_mongodb_test.yaml",
                "secrets_mongodb_test.yaml",
                "configmap_mongodb_test.yaml"
        );

        lintAndTestChart(
                new HelmUnittestContext(
                        "test-mongodb-chart",
                        "1.0.0",
                        starterDependencies,
                        "mongodb",
                        unittestFiles)
        );
    }

}
