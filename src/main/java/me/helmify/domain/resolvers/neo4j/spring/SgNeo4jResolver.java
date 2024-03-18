package me.helmify.domain.resolvers.neo4j.spring;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.resolvers.neo4j.Neo4jResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static me.helmify.util.HelmUtil.makeSecretKeyRef;

/**
 * Resolver for spring neo4j dependency.
 */
@Component
public class SgNeo4jResolver implements Neo4jResolver {

	//@formatter:off

    @Override
    public List<String> matchOn() {
        return List.of("spring-boot-starter-data-neo4j");
    }


    @Override public List<Map<String,Object>> getEnvironmentEntries(HelmContext context) {
        return List.of(
                makeSecretKeyRef("SPRING_NEO4J_AUTHENTICATION_USERNAME", "neo4j-username", context.getAppName()),
                makeSecretKeyRef("SPRING_NEO4J_AUTHENTICATION_PASSWORD", "neo4j-password", context.getAppName()));
    }

    @Override
    public Map<String,Object> getSecretEntries() {
            return Map.of(
                    "neo4j-username", "{{ .Values.neo4j.neo4j.user | b64enc | quote }}",
                    "neo4j-password", "{{ .Values.neo4j.neo4j.password | b64enc | quote }}");

    }
    @Override public Map<String,String> getDefaultConfig() {
        return Map.of(
                "spring.neo4j.uri",
                "bolt://{{ .Values.global.hosts.neo4j }}:{{ .Values.global.ports.neo4j }}"
        );
    }


}
