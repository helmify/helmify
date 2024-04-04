package me.helmify.domain.helm.dependencies.cassandra.spring;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.cassandra.CassandraResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static me.helmify.util.HelmUtil.makeSecretKeyRef;

@Component
public class SgCassandraResolver implements CassandraResolver {

	//@formatter:off
    @Override
    public List<String> matchOn() {
        return List.of("spring-boot-starter-data-cassandra", "spring-boot-starter-data-cassandra-reactive");
    }

    public Map<String, Object> getSecretEntries() {
        return Map.of(
                "SPRING_CASSANDRA_USERNAME", "{{ .Values.cassandra.dbUser.user | b64enc | quote }}",
                "SPRING_CASSANDRA_PASSWORD", "{{ .Values.cassandra.dbUser.password | b64enc | quote }}"
        );
    }


    public Map<String, String> getDefaultConfig() {
        return Map.of(
                "SPRING_CASSANDRA_CONTACT-POINTS","{{ .Values.global.hosts.cassandra }}.{{ .Release.Namespace }}.svc.cluster.local",
                "SPRING_CASSANDRA_KEYSPACE-NAME", "{{ .Values.cassandra.keyspaceName }}",
                "SPRING_CASSANDRA_LOCAL-DATACENTER", "{{ .Values.cassandra.dataCenter }}"
        );
    }

}
