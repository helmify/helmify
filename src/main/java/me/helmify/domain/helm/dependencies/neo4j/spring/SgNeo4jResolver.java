package me.helmify.domain.helm.dependencies.neo4j.spring;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.neo4j.Neo4jResolver;
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



    @Override
    public Map<String,Object> getSecretEntries() {
            return Map.of(
                    "SPRING_NEO4J_AUTHENTICATION_USERNAME", "{{ .Values.neo4j.neo4j.user | b64enc | quote }}",
                    "SPRING_NEO4J_AUTHENTICATION_PASSWORD", "{{ .Values.neo4j.neo4j.password | b64enc | quote }}");

    }
    @Override public Map<String,String> getDefaultConfig() {
        return Map.of(
                "SPRING_NEO4J_URI",
                "bolt://{{ .Values.global.hosts.neo4j }}:{{ .Values.global.ports.neo4j }}"
        );
    }


}
