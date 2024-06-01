package me.helmify.domain.helm.dependencies.mongodb;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.DependencyResolver;

import java.util.List;
import java.util.Map;

public interface MongodbResolver extends DependencyResolver {

	//@formatter:off
	default Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of("mongodb", Map.of(
						"enabled", true,
						"database", "db",
						"fullnameOverride", context.getAppName() + "-mongodb",
						"nameOverride", context.getAppName() + "-mongodb",
						"architecture", "standalone",
						"primary", Map.of(
							"persistence", Map.of(
								"enabled", true,
								"storageClass", "",
								"accessModes", List.of("ReadWriteOnce"),
								"size", "1Gi")),
						"auth", Map.of(
								"usernames", List.of("mongodb"),
								"passwords", List.of("mongodb"),
								"rootPassword", "mongodb",
								"databases", List.of("db"))),
					"global", Map.of(
							"hosts", Map.of(
									"mongodb", getHost(context)),
							"ports", Map.of(
									"mongodb", getPort())));
	}

	@Override
	default String getHost(HelmContext context) {
		return context.getAppName() + "-mongodb";
	}

	@Override
	default Integer getPort() {
		return 27017;
	}

	default Map<String, Object> getPreferredChart() {
		return Map.of("name", "mongodb", "version", "14.0.11", "repository", "https://charts.bitnami.com/bitnami");
	}

	@Override
	default String dependencyName() {
		return "mongodb";
	}

}
