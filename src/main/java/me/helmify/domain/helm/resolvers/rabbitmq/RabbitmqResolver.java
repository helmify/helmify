package me.helmify.domain.helm.resolvers.rabbitmq;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.resolvers.DependencyResolver;

import java.util.Map;

public interface RabbitmqResolver extends DependencyResolver {

	@Override
	default String dependencyName() {
		return "rabbitmq";
	}

	default Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of("rabbitmq",
				Map.of("enabled", true, "port", 5672, "vhost", "/", "nameOverride", context.getAppName() + "-rabbitmq",
						"fullnameOverride", context.getAppName() + "-rabbitmq", "auth",
						Map.of("username", "guest", "password", "guest")),
				"global", Map.of("hosts", Map.of("rabbitmq", context.getAppName() + "-rabbitmq"), "ports",
						Map.of("rabbitmq", 5672)));
	}

	default Map<String, String> getPreferredChart() {
		return Map.of("name", "rabbitmq", "version", "11.9.0", "repository", "https://charts.bitnami.com/bitnami");
	}

}
