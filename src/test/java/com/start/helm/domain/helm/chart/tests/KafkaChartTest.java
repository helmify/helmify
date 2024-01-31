package com.start.helm.domain.helm.chart.tests;

import org.junit.jupiter.api.Test;

import java.util.List;

public class KafkaChartTest extends HelmChartTests {

	@Test
	public void testChartWithKafka() {

		List<String> starterDependencies = List.of("kafka");
		//@formatter:off
        List<String> unittestFiles = List.of(
                "deployment_kafka_test.yaml",
                "service_kafka_test.yaml",
                "configmap_kafka_test.yaml"
        );

        lintAndTestChart(
                new HelmUnittestContext(
                        "test-kafka-chart",
                        "1.0.0",
                        starterDependencies,
                        "kafka",
                        unittestFiles)
        );
    }

}
