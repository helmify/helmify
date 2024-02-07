package com.start.helm.domain.resolvers.postgres;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.resolvers.DependencyResolver;

import java.util.List;
import java.util.Map;

public interface PostgresResolver extends DependencyResolver {

	//@formatter:off

	default Map<String, String> getPreferredChart() {
		return Map.of(
				"name", "postgresql",
				"version", "11.9.2",
				"repository", "https://charts.bitnami.com/bitnami");
	}

	default Map<String, Object> getValuesEntries(HelmContext context) {
		final String name = context.getAppName() + "-postgresql";
		return Map.of(
				"postgresql", Map.of(
						"enabled", true,
						"database", "postgres",
						"fullnameOverride", name,
						"nameOverride", name,
						"architecture", "standalone",
						"primary", Map.of(
							"persistence", Map.of(
									"enabled", true,
									"storageClass", "",
									"size", "1Gi",
									"accessModes", List.of("ReadWriteOnce")
							)
						),
					"auth", Map.of(
							"username", "postgres",
							"password", "postgres"
						)
				), "global", Map.of(
						"hosts", Map.of("postgresql", name),
						"ports", Map.of("postgresql", 5432)
				)
		);
	}

	@Override
	default String dependencyName() {
		return "postgresql";
	}

}
