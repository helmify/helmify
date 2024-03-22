package me.helmify.domain.helm.dependencies.neo4j;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.DependencyResolver;

import java.util.Map;

public interface Neo4jResolver extends DependencyResolver {

	//@formatter:off
    @Override
    default String dependencyName() {
        return "neo4j";
    }

    @Override
    default Map<String, Object> getValuesEntries(HelmContext context) {
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
    @Override
    default Map<String, String> getPreferredChart() {
        return Map.of(
                "name", "neo4j",
                "version", "5.12.0",
                "repository", "https://helm.neo4j.com/neo4j"
        );
    }
}
