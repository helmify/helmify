package com.start.helm.domain.resolvers.cassandra;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.resolvers.DependencyResolver;

import java.util.Map;

public interface CassandraResolver extends DependencyResolver {

	//@formatter:off

	default Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of("cassandra",
				Map.of(	"enabled", true,
						"keyspaceName", context.getAppName(),
						"service", Map.of(
								"ports", Map.of(
										"cql", getPort()
								)
						),
						"port", getPort(),
						"nameOverride", context.getAppName() + "-cassandra",
						"fullnameOverride", context.getAppName() + "-cassandra",
						"dbUser", Map.of(
								"user", "cassandra",
								"password", "cassandra"
						)
				),
				"global", Map.of(
						"hosts", Map.of(
								"cassandra", context.getAppName() + "-cassandra"),
						"ports", Map.of(
								"cassandra", getPort())));
	}

	default Map<String, String> getPreferredChart() {
		return Map.of(
				"name", "cassandra",
				"version", "4.1.4",
				"repository", "https://charts.bitnami.com/bitnami"
		);
	}

	default int getPort() {
		return 9042;
	}

	@Override
	default String dependencyName() {
		return "cassandra";
	}

}
