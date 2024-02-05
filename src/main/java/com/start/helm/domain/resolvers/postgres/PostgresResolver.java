package com.start.helm.domain.resolvers.postgres;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.resolvers.DependencyResolver;

import java.util.List;
import java.util.Map;

public interface PostgresResolver extends DependencyResolver {

	default Map<String, String> getPreferredChart() {
		return Map.of("name", "postgresql", "version", "11.9.2", "repository", "https://charts.bitnami.com/bitnami");
	}

	default Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of("postgresql", Map.of("enabled", true, "database", "postgres", "fullnameOverride",
				context.getAppName() + "-postgresql", "nameOverride", context.getAppName() + "-postgresql",
				"architecture", "standalone", "primary",
				Map.of("persistence",
						Map.of("enabled", true, "storageClass", "", "accessModes", List.of("ReadWriteOnce"), "size",
								"1Gi")),
				"auth", Map.of("username", "postgres", "password", "postgres")), "global",
				Map.of("hosts", Map.of("postgresql", context.getAppName() + "-postgresql"), "ports",
						Map.of("postgresql", 5432)));
	}

	@Override
	default String dependencyName() {
		return "postgresql";
	}

}
