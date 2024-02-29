package com.start.helm.domain.resolvers.cassandra.spring;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.resolvers.cassandra.CassandraResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.start.helm.util.HelmUtil.makeSecretKeyRef;

@Component
public class SgCassandraResolver implements CassandraResolver {

	//@formatter:off
    @Override
    public List<String> matchOn() {
        return List.of("spring-boot-starter-data-cassandra", "spring-boot-starter-data-cassandra-reactive");
    }


    public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
        return List.of(
                makeSecretKeyRef("SPRING_CASSANDRA_USERNAME", "cassandra-username", context.getAppName()),
                makeSecretKeyRef("SPRING_CASSANDRA_PASSWORD", "cassandra-password", context.getAppName())
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
                "spring.cassandra.contact-points","{{ .Values.global.hosts.cassandra }}:{{ .Values.global.ports.cassandra }}",
                "spring.cassandra.keyspace-name", "{{ .Values.cassandra.keyspaceName }}"
        );
    }

}
