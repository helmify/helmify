package com.start.helm.domain.resolvers;

import com.start.helm.domain.helm.HelmContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.start.helm.util.HelmUtil.makeSecretKeyRef;

/**
 * Resolver for spring neo4j dependency.
 */
@Component
public class Neo4jDependencyResolver implements DependencyResolver {

	//@formatter:off

    @Override
    public List<String> matchOn() {
        return List.of("spring-boot-starter-data-neo4j");
    }

    @Override
    public String dependencyName() {
        return "neo4j";
    }

    @Override
    public Map<String, Object> getValuesEntries(HelmContext context) {
        return Map.of(
                    "global", Map.of(
                        "hosts", Map.of("neo4j", context.getAppName() + "-neo4j"),
                        "ports", Map.of("neo4j", 7687)),
                    "neo4j", Map.of(
                        "enabled", true,
                        "database", "my_database",
                        "fullnameOverride", context.getAppName() + "-neo4j",
                        "nameOverride", context.getAppName() + "-neo4j",
                        "neo4j", Map.of(
                            "edition", "community",
                            "name",context.getAppName() + "-neo4j",
                            "user", "neo4j",
                            "password", context.getAppName() + "-neo4j-password"
                        ),
                        "volumes", Map.of(
                            "data", Map.of(
                                "labels", Map.of("data", "true"),
                                "mode", "defaultStorageClass",
                                "defaultStorageClass", Map.of("requests",
                                        Map.of("storage", "2Gi")
                                    )
                                )
                        )
                )
        );
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

    @Override
    public Map<String, String> getPreferredChart() {
        return Map.of(
                "name", "neo4j",
                "version", "5.12.0",
                "repository", "https://helm.neo4j.com/neo4j"
        );
    }
}
