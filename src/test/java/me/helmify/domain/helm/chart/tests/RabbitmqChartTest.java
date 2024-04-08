package me.helmify.domain.helm.chart.tests;

import me.helmify.domain.helm.HelmChartTests;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RabbitmqChartTest extends HelmChartTests {

	@Test
	public void testChartWithRabbitmq() {

		List<String> starterDependencies = List.of("amqp");
		//@formatter:off
        List<String> unittestFiles = List.of(
                "deployment_rabbitmq_test.yaml",
                "service_rabbitmq_test.yaml",
                "configmap_rabbitmq_test.yaml",
                "secrets_rabbitmq_test.yaml"
        );

        lintAndTestChart(
                new HelmUnittestContext(
                        "test-rabbitmq-chart",
                        "1.0.0",
                        starterDependencies,
                        "rabbitmq",
                        unittestFiles , "helm", FrameworkVendor.Spring)
        );
    }

}
