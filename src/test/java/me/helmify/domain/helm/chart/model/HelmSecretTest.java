package me.helmify.domain.helm.chart.model;

import me.helmify.domain.helm.model.HelmSecret;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HelmSecretTest {

	@Test
	void getYaml() {

		HelmSecret helmSecret = new HelmSecret();
		helmSecret.setFileName("test");
		helmSecret.setSecretName("test");
		helmSecret.setStringData("""
				CASSANDRA_KEYSPACE: {{ .Values.cassandra.keyspaceName }}
				CASSANDRA_REPLICATION_STRATEGY: SimpleStrategy
				CASSANDRA_REPLICATION_FACTOR: "1"
				CASSANDRA_REPLICATION_OPTIONS: '{"class":"SimpleStrategy", "replication_factor":1}'
				                """);
		String yaml = helmSecret.getYaml();
		System.out.println(yaml);

		Assertions.assertTrue(yaml.contains("  CASSANDRA_KEYSPACE"));
		Assertions.assertTrue(yaml.contains("  CASSANDRA_REPLICATION_STRATEGY"));
		Assertions.assertTrue(yaml.contains("  CASSANDRA_REPLICATION_FACTOR"));
		Assertions.assertTrue(yaml.contains("  CASSANDRA_REPLICATION_OPTIONS"));
		Assertions.assertTrue(yaml.contains("  name: test"));

	}

}
