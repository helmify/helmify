package me.helmify.domain.helm.bitnami;

import me.helmify.domain.helm.HelmChartTests;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.postgres.quarkus.QsPostgresResolver;
import me.helmify.domain.helm.dependencies.postgres.spring.SgPostgresResolver;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CouchbaseaBitnamiChartTest extends HelmChartTests {

	String bitnami = "bitnami";

	String testSource = "couchbase";

	String chartName = "test-couchbase-chart";

	@Test
	public void lintBitnamiChartSpringCouchbase() {
		List<String> starterDependencies = List.of("data-couchbase");
		lintAndTestChart(new HelmUnittestContext(chartName, "1.0.0", starterDependencies, testSource, List.of(),
				bitnami, FrameworkVendor.Spring));
	}

	public void lintBitnamiChartQuarkusMariadb() {
		List<String> starterDependencies = List.of("elasticsearch-rest-client");
		lintAndTestChart(new HelmUnittestContext(chartName, "1.0.0", starterDependencies, testSource, List.of(),
				bitnami, FrameworkVendor.Quarkus));
	}

}
