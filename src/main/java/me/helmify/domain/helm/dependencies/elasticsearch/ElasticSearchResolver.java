package me.helmify.domain.helm.dependencies.elasticsearch;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.DependencyResolver;

import java.util.Map;

public interface ElasticSearchResolver extends DependencyResolver {

	default Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of("elasticsearch",
				Map.of("enabled", true, "port", getPort(), "nameOverride", context.getAppName() + "-elasticsearch",
						"fullnameOverride", context.getAppName() + "-elasticsearch", "security",
						Map.of("username", "elastic", "elasticPassword", "elasticsearch")),
				"global", Map.of("hosts", Map.of("elasticsearch", context.getAppName() + "-elasticsearch"), "ports",
						Map.of("elasticsearch", getPort())));
	}

	default Map<String, String> getPreferredChart() {
		return Map.of("name", "elasticsearch", "version", "19.19.3", "repository",
				"https://charts.bitnami.com/bitnami");
	}

	default int getPort() {
		return 9200;
	}

	@Override
	default String dependencyName() {
		return "elasticsearch";
	}

}
