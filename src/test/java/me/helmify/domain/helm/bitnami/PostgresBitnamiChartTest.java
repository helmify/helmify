package me.helmify.domain.helm.bitnami;

import me.helmify.domain.helm.HelmChartTests;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.postgres.quarkus.QsPostgresResolver;
import me.helmify.domain.helm.dependencies.postgres.spring.SgPostgresResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

@Execution(ExecutionMode.CONCURRENT)
public class PostgresBitnamiChartTest extends HelmChartTests {

	String bitnami = "bitnami";

	@Test
	public void lintBitnamiChartSpringPostgres() {
		List<String> starterDependencies = List.of(new SgPostgresResolver().matchOn().iterator().next());
		lintAndTestChart(new HelmUnittestContext("test-postgres-chart", "1.0.0", starterDependencies, "postgres",
				List.of(), bitnami, FrameworkVendor.Spring));
	}

	@Test
	public void lintBitnamiChartQuarkusPostgres() {
		List<String> starterDependencies = List.of(new QsPostgresResolver().matchOn().iterator().next());
		lintAndTestChart(new HelmUnittestContext("test-postgres-chart", "1.0.0", starterDependencies, "postgres",
				List.of(), bitnami, FrameworkVendor.Quarkus));
	}

}
