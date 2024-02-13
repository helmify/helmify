package com.start.helm.domain.resolvers.redis;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.resolvers.DependencyResolver;

import java.util.Map;

public interface RedisResolver extends DependencyResolver {

	@Override
	default String dependencyName() {
		return "redis";
	}

	@Override
	default Map<String, String> getPreferredChart() {
		return Map.of("name", "redis", "version", "18.1.2", "repository", "https://charts.bitnami.com/bitnami");
	}

	@Override
	default Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of("redis",
				Map.of("enabled", true, "port", 6379, "nameOverride", context.getAppName() + "-redis",
						"fullnameOverride", context.getAppName() + "-redis", "architecture", "standalone"),
				"global",
				Map.of("hosts", Map.of("redis", getRedisHost(context)), "ports", Map.of("redis", getRedisPort())));
	}

	private int getRedisPort() {
		return 6379;
	}

	private String getRedisHost(HelmContext context) {
		return context.getAppName() + "-redis-master";
	}

}
