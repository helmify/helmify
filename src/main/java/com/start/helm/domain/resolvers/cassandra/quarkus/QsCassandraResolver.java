package com.start.helm.domain.resolvers.cassandra.quarkus;

import com.start.helm.domain.FrameworkVendor;
import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.resolvers.cassandra.CassandraResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.start.helm.util.HelmUtil.makeSecretKeyRef;

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


    public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
        return List.of(
                makeSecretKeyRef("QUARKUS_CASSANDRA_AUTH_USERNAME", "cassandra-username", context.getAppName()),
                makeSecretKeyRef("QUARKUS_CASSANDRA_AUTH_PASSWORD", "cassandra-password", context.getAppName())
        );
    }


    public Map<String, Object> getSecretEntries() {
        return Map.of(
                "cassandra-username", "{{ .Values.cassandra.dbUser.user | b64enc | quote }}",
                "cassandra-password", "{{ .Values.cassandra.dbUser.password | b64enc | quote }}"
        );
    }


    public Map<String, String> getDefaultConfig() {
        return Map.of(
                "quarkus.cassandra.contact-points","{{ .Values.global.hosts.cassandra }}.{{ .Release.Namespace }}.svc.cluster.local:{{ .Values.global.ports.cassandra }}",
                "quarkus.cassandra.keyspace", "{{ .Values.cassandra.keyspaceName }}",
                "quarkus.cassandra.local-datacenter", "{{ .Values.cassandra.dataCenter }}"
        );
    }

    @Override
    public FrameworkVendor getVendor() {
        return FrameworkVendor.Quarkus;
    }

}
