package me.helmify.domain.helm.dependencies.elasticsearch;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.DependencyResolver;

import java.util.Map;

public interface ElasticSearchResolver extends DependencyResolver {

	//@formatter:off
	default Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of(
				"elasticsearch", Map.of(
						"enabled", true,
						"port", getPort(),
						"nameOverride", context.getAppName() + "-elasticsearch",
						"fullnameOverride", context.getAppName() + "-elasticsearch",
						"security", Map.of(
								"username", "elastic",
								"elasticPassword", "elasticsearch"
						), "master", Map.of(
								"masterOnly", false,
								"replicaCount", 1
						), "data", Map.of(
								"replicaCount", 0
						), "coordinating", Map.of(
								"replicaCount", 0
						), "ingest", Map.of(
								"replicaCount", 0
						)
				),
				"global", Map.of(
						"hosts", Map.of(
								"elasticsearch", getHost(context)),
						"ports", Map.of("elasticsearch", getPort())));


	}

	default Map<String, Object> getPreferredChart() {
		return Map.of(
				"name", "elasticsearch",
				"version", "19.19.3",
				"repository", "https://charts.bitnami.com/bitnami");
	}

	default Integer getPort() {
		return 9200;
	}

	@Override default String getHost(HelmContext context) {
		return context.getAppName() + "-elasticsearch-master-hl";
    }

	@Override
	default String dependencyName() {
		return "elasticsearch";
	}

 }
