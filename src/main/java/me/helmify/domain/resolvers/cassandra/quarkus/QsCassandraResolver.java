package me.helmify.domain.resolvers.cassandra.quarkus;

import me.helmify.domain.FrameworkVendor;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.resolvers.cassandra.CassandraResolver;
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


    public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
        return List.of(
                HelmUtil.makeSecretKeyRef("QUARKUS_CASSANDRA_AUTH_USERNAME", "cassandra-username", context.getAppName()),
                HelmUtil.makeSecretKeyRef("QUARKUS_CASSANDRA_AUTH_PASSWORD", "cassandra-password", context.getAppName())
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
                "%prod.quarkus.cassandra.contact-points","{{ .Values.global.hosts.cassandra }}.{{ .Release.Namespace }}.svc.cluster.local:{{ .Values.global.ports.cassandra }}",
                "%prod.quarkus.cassandra.keyspace", "{{ .Values.cassandra.keyspaceName }}",
                "%prod.quarkus.cassandra.local-datacenter", "{{ .Values.cassandra.dataCenter }}"
        );
    }

    @Override
    public FrameworkVendor getVendor() {
        return FrameworkVendor.Quarkus;
    }

}
