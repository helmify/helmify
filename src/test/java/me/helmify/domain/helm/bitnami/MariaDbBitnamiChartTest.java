package me.helmify.domain.helm.bitnami;

import me.helmify.domain.helm.HelmChartTests;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.postgres.quarkus.QsPostgresResolver;
import me.helmify.domain.helm.dependencies.postgres.spring.SgPostgresResolver;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MariaDbBitnamiChartTest extends HelmChartTests {

	String bitnami = "bitnami";

	String testSource = "mariadb";

	@Test
	public void lintBitnamiChartSpringMariadb() {
		List<String> starterDependencies = List.of("mariadb");
		lintAndTestChart(new HelmUnittestContext("test-mariadb-chart", "1.0.0", starterDependencies, testSource,
				List.of(), bitnami, FrameworkVendor.Spring));
	}

	@Test
	public void lintBitnamiChartQuarkusMariadb() {
		List<String> starterDependencies = List.of("jdbc-mariadb");
		lintAndTestChart(new HelmUnittestContext("test-mariadb-chart", "1.0.0", starterDependencies, testSource,
				List.of(), bitnami, FrameworkVendor.Quarkus));
	}

}
