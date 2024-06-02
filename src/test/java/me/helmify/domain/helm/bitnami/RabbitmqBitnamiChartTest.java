package me.helmify.domain.helm.bitnami;

import me.helmify.domain.helm.HelmChartTests;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.postgres.quarkus.QsPostgresResolver;
import me.helmify.domain.helm.dependencies.postgres.spring.SgPostgresResolver;
import me.helmify.domain.helm.dependencies.rabbitmq.quarkus.QsRabbitmqResolver;
import me.helmify.domain.helm.dependencies.rabbitmq.spring.SgRabbitmqResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

@Execution(ExecutionMode.CONCURRENT)
public class RabbitmqBitnamiChartTest extends HelmChartTests {

	String bitnami = "bitnami";

	@Test
	public void lintBitnamiChartSpringRabbit() {
		List<String> starterDependencies = List.of("amqp");
		lintAndTestChart(new HelmUnittestContext("test-rabbitmq-chart", "1.0.0", starterDependencies, "rabbitmq",
				List.of(), bitnami, FrameworkVendor.Spring));
	}

	@Test
	public void lintBitnamiChartQuarkusRabbit() {
		List<String> starterDependencies = List.of("messaging-rabbitmq");
		lintAndTestChart(new HelmUnittestContext("test-rabbitmq-chart", "1.0.0", starterDependencies, "rabbitmq",
				List.of(), bitnami, FrameworkVendor.Quarkus));
	}

}
