package me.helmify.domain.helm.dependencies.redis;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.DependencyResolver;

import java.util.Map;

public interface RedisResolver extends DependencyResolver {

	@Override
	default String dependencyName() {
		return "redis";
	}

	@Override
	default Map<String, Object> getPreferredChart() {
		return Map.of("name", "redis", "version", "18.1.2", "repository", "https://charts.bitnami.com/bitnami");
	}

	//@formatter:off
	@Override
	default Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of("redis",
				Map.of( "enabled", true,
						"auth", Map.of("enabled", true, "password", "redis"),
						"port", getPort(),
						"nameOverride", context.getAppName() + "-redis",
						"fullnameOverride", context.getAppName() + "-redis",
						"architecture", "standalone"
				),
				"global", Map.of(
					"hosts", Map.of(
						"redis", getHost(context)),
						"ports", Map.of("redis", getPort()))
		);
	}

	@Override
	default Integer getPort() {
    	return 6379;
    }

	@Override
	default String getHost(HelmContext context) {
		return context.getAppName() + "-redis-master";
    }

}
