package me.helmify.domain.resolvers.cassandra;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.chart.model.HelmSecret;
import me.helmify.domain.resolvers.DependencyResolver;

import java.util.List;
import java.util.Map;

public interface CassandraResolver extends DependencyResolver {

	//@formatter:off

	default String getKeySpaceName(HelmContext context) {
		return context.getAppName().replace("-", "");
	}

	default Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of("cassandra",
				Map.of(	"enabled", true,
						"keyspaceName", getKeySpaceName(context),
						"dataCenter", "datacenter1",
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
						),
						"initDBSecret", "cassandra-init"
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
				"version", "10.11.2",
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

	//@formatter:off
	@Override
	default List<HelmSecret> getExtraSecrets(HelmContext context) {

		return List.of(
				new HelmSecret(
						"cassandra-init-secret.yaml",
						"cassandra-init",
						"""
init-keyspace.cql: >-
  {{ "CREATE KEYSPACE IF NOT EXISTS @@keyspace WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1};" | b64enc}}
        """.replace("@@keyspace", getKeySpaceName(context))
				)
		);
	}}
