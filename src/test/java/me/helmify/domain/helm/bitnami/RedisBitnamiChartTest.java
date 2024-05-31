package me.helmify.domain.helm.bitnami;

import me.helmify.domain.helm.HelmChartTests;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.postgres.quarkus.QsPostgresResolver;
import me.helmify.domain.helm.dependencies.postgres.spring.SgPostgresResolver;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RedisBitnamiChartTest extends HelmChartTests {

	String bitnami = "bitnami";

	String testSource = "redis";

	@Test
	public void lintBitnamiChartSpringRedis() {
		List<String> starterDependencies = List.of(testSource);
		lintAndTestChart(new HelmUnittestContext("test-redis-chart", "1.0.0", starterDependencies, testSource,
				List.of(), bitnami, FrameworkVendor.Spring));
	}

	@Test
	public void lintBitnamiChartQuarkusRedis() {
		List<String> starterDependencies = List.of("redis-client");
		lintAndTestChart(new HelmUnittestContext("test-redis-chart", "1.0.0", starterDependencies, "redis", List.of(),
				bitnami, FrameworkVendor.Quarkus));
	}

}
