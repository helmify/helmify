package me.helmify.domain.helm.dependencies.cassandra.quarkus;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.cassandra.CassandraResolver;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class QsCassandraResolver implements CassandraResolver {

	/**
	 * quarkus.cassandra.contact-points={cassandra_ip}:9042
	 * quarkus.cassandra.local-datacenter={dc_name} quarkus.cassandra.keyspace={keyspace}
	 * quarkus.cassandra.auth.username=john quarkus.cassandra.auth.password=s3cr3t
	 */
	//@formatter:off
    @Override
    public List<String> matchOn() {
        return List.of("cassandra-quarkus-client");
    }


    public Map<String, Object> getSecretEntries() {
        return Map.of(
                "QUARKUS_CASSANDRA_AUTH_USERNAME", "{{ .Values.cassandra.dbUser.user | b64enc | quote }}",
                "QUARKUS_CASSANDRA_AUTH_PASSWORD", "{{ .Values.cassandra.dbUser.password | b64enc | quote }}"
        );
    }

    public Map<String, String> getDefaultConfig() {
        return Map.of(
                "QUARKUS_CASSANDRA_CONTACT-POINTS","{{ .Values.global.hosts.cassandra }}.{{ .Release.Namespace }}.svc.cluster.local:{{ .Values.global.ports.cassandra }}",
                "QUARKUS_CASSANDRA_KEYSPACE", "{{ .Values.cassandra.keyspaceName }}",
                "QUARKUS_CASSANDRA_LOCAL-DATACENTER", "{{ .Values.cassandra.dataCenter }}"
        );
    }

    @Override
    public FrameworkVendor getVendor() {
        return FrameworkVendor.Quarkus;
    }

}
