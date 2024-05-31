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
public class MysqlBitnamiChartTest extends HelmChartTests {

	String bitnami = "bitnami";

	@Test
	public void lintBitnamiChartSpringMysql() {
		List<String> starterDependencies = List.of("mysql");
		lintAndTestChart(new HelmUnittestContext("test-mysql-chart", "1.0.0", starterDependencies, "mysql", List.of(),
				bitnami, FrameworkVendor.Spring));
	}

	@Test
	public void lintBitnamiChartQuarkusMysql() {
		List<String> starterDependencies = List.of("jdbc-mysql");
		lintAndTestChart(new HelmUnittestContext("test-mysql-chart", "1.0.0", starterDependencies, "mysql", List.of(),
				bitnami, FrameworkVendor.Quarkus));
	}

}
