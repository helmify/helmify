package me.helmify.domain.helm.chart.tests;

import me.helmify.domain.helm.HelmChartTests;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ActuatorPostgresChartTest extends HelmChartTests {

	@Test
	public void testChartWithPostgres() {

		List<String> starterDependencies = List.of("postgresql", "actuator", "web");
		//@formatter:off
        List<String> unittestFiles = List.of(
                "deployment_actuator_test.yaml",
                "service_actuator_test.yaml",
                "configmap_actuator_test.yaml"
        );

        lintAndTestChart(
                new HelmUnittestContext(
                        "test-postgres-actuator-chart",
                        "1.0.0",
                        starterDependencies,
                        "actuator",
                        unittestFiles)
        );
    }

}
