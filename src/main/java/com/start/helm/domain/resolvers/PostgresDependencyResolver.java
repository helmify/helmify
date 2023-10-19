package com.start.helm.domain.resolvers;

import com.start.helm.domain.helm.HelmContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.start.helm.util.HelmUtil.makeSecretKeyRef;

/**
 * Resolver for spring postgres dependency.
 */
@Component
public class PostgresDependencyResolver implements DependencyResolver {

    @Override
    public List<String> matchOn() {
        return List.of("postgres");
    }


    public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
        return List.of(
                makeSecretKeyRef("SPRING_DATASOURCE_USERNAME", "postgres-username", context.getAppName()),
                makeSecretKeyRef("SPRING_DATASOURCE_PASSWORD", "postgres-password", context.getAppName())
        );
    }


    public Map<String, Object> getSecretEntries() {
        return Map.of(
                "postgres-username", "{{ .Values.postgresql.auth.username | b64enc | quote }}"
                , "postgres-password", "{{ .Values.postgresql.auth.password | b64enc | quote }}"
        );
    }


    public Map<String, String> getDefaultConfig() {
        return Map.of(
                "spring.datasource.url",
                "jdbc:postgresql://{{ .Values.global.hosts.postgresql }}:{{ .Values.global.ports.postgresql }}/{{ .Values.postgresql.database }}"
        );
    }

    public Map<String, Object> getValuesEntries(HelmContext context) {
        return Map.of("postgresql", Map.of(
                        "enabled", true,
                        "database", "postgres",
                        "fullnameOverride", context.getAppName() + "-postgresql",
                        "nameOverride", context.getAppName() + "-postgresql",
                        "architecture", "standalone",
                        "primary", Map.of(
                                "persistence", Map.of(
                                        "enabled", true,
                                        "storageClass", "",
                                        "accessModes", List.of("ReadWriteOnce"),
                                        "size", "1Gi"
                                )
                        ),
                        "auth",
                        Map.of("username", "postgres", "password", "postgres")),
                "global",
                Map.of("hosts", Map.of("postgresql", context.getAppName() + "-postgresql"), "ports", Map.of("postgresql", 5432))
        );
    }

    public Map<String, String> getPreferredChart() {
        return Map.of(
                "name", "postgresql",
                "version", "11.9.2",
                "repository", "https://charts.bitnami.com/bitnami"
        );
    }

    @Override
    public String dependencyName() {
        return "postgresql";
    }
}
