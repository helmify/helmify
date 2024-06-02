package me.helmify.domain.helm.dependencies.mysql;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.DependencyResolver;

import java.util.List;
import java.util.Map;

public interface MySqlResolver extends DependencyResolver {

	default Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of("mysql",
				Map.of("enabled", true, "database", "my_database", "fullnameOverride", context.getAppName() + "-mysql",
						"nameOverride", context.getAppName() + "-mysql", "architecture", "standalone", "primary",
						Map.of("persistence",
								Map.of("enabled", true, "storageClass", "", "accessModes", List.of("ReadWriteOnce"),
										"size", "1Gi")),
						"auth", Map.of("username", "mysql", "password", "mysql", "rootPassword", "mysql")),
				"global", Map.of("hosts", Map.of("mysql", getHost(context)), "ports", Map.of("mysql", getPort())));
	}

	default Map<String, Object> getPreferredChart() {
		return Map.of("name", "mysql", "version", "9.12.5", "repository", "https://charts.bitnami.com/bitnami");
	}

	@Override
	default String dependencyName() {
		return "mysql";
	}

	@Override
	default String getHost(HelmContext context) {
		return context.getAppName() + "-mysql";
	}

	@Override
	default Integer getPort() {
		return 3306;
	}

}
