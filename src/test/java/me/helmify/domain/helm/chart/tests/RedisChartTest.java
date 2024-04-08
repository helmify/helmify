package me.helmify.domain.helm.chart.tests;

import me.helmify.domain.helm.HelmChartTests;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RedisChartTest extends HelmChartTests {

	@Test
	public void testChartWithRedis() {

		List<String> starterDependencies = List.of("data-redis");
		//@formatter:off
        List<String> unittestFiles = List.of(
                "deployment_redis_test.yaml",
                "service_redis_test.yaml",
                "configmap_redis_test.yaml"
        );

        lintAndTestChart(
                new HelmUnittestContext(
                        "test-redis-chart",
                        "1.0.0",
                        starterDependencies,
                        "redis",
                        unittestFiles)
        );
    }

}
