package me.helmify.domain.helm.dependencies.rabbitmq;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.DependencyResolver;

import java.util.Map;

public interface RabbitmqResolver extends DependencyResolver {

	@Override
	default String dependencyName() {
		return "rabbitmq";
	}

	//@formatter:off
	default Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of("rabbitmq", Map.of(
				"enabled", true,
				"port", getPort(),
				"vhost", "/",
				"nameOverride", context.getAppName() + "-rabbitmq",
				"fullnameOverride", context.getAppName() + "-rabbitmq",
				"auth", Map.of(
						"username", "guest",
						"password", "guest")
				),
				"global", Map.of(
						"hosts", Map.of(
								"rabbitmq", getHost(context)),
						"ports", Map.of(
								"rabbitmq", getPort())));
	}

	default Map<String, Object> getPreferredChart() {
		return Map.of("name", "rabbitmq", "version", "11.9.0", "repository", "https://charts.bitnami.com/bitnami");
	}

	@Override
	default Integer getPort() {
		return 5672;
	}

	@Override
	default String getHost(HelmContext context) {
		return context.getAppName() + "-rabbitmq";
	}
}
