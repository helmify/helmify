package me.helmify.domain.helm.resolvers.mariadb;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.resolvers.DependencyResolver;

import java.util.List;
import java.util.Map;

public interface MariaDbResolver extends DependencyResolver {

	default Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of("mariadb",
				Map.of("enabled", true, "database", "my_database", "fullnameOverride",
						context.getAppName() + "-mariadb", "nameOverride", context.getAppName() + "-mariadb",
						"architecture", "standalone", "primary",
						Map.of("persistence",
								Map.of("enabled", true, "storageClass", "", "accessModes", List.of("ReadWriteOnce"),
										"size", "1Gi")),
						"auth", Map.of("username", "mariadb", "password", "mariadb", "rootPassword", "mariadb")),
				"global", Map.of("hosts", Map.of("mariadb", context.getAppName() + "-mariadb"), "ports",
						Map.of("mariadb", 3306)));
	}

	default Map<String, String> getPreferredChart() {
		return Map.of("name", "mariadb", "version", "14.0.3", "repository", "https://charts.bitnami.com/bitnami");
	}

	@Override
	default String dependencyName() {
		return "mariadb";
	}

}
