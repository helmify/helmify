package me.helmify.domain.helm.bitnami.lint;

import me.helmify.domain.helm.HelmChartTests;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BitnamiPostgresLintTest extends HelmChartTests {

	@Test
	public void testChartWithPostgres() {

		List<String> starterDependencies = List.of("postgresql");

		lintAndTestChart(
				new HelmUnittestContext("test-postgres-chart", "1.0.0", starterDependencies, "postgres", List.of()));
	}

}
