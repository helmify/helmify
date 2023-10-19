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
public class MySQLDependencyResolver implements DependencyResolver {

    @Override
    public List<String> matchOn() {
        return List.of("mysql-connector-j");
    }


    public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
        return List.of(
                makeSecretKeyRef("SPRING_DATASOURCE_USERNAME", "mysql-username", context.getAppName()),
                makeSecretKeyRef("SPRING_DATASOURCE_PASSWORD", "mysql-password", context.getAppName())
        );
    }


    public Map<String, Object> getSecretEntries() {
        return Map.of(
                "mysql-username", "{{ .Values.mysql.auth.username | b64enc | quote }}"
                , "mysql-password", "{{ .Values.mysql.auth.password | b64enc | quote }}"
        );
    }


    public Map<String, String> getDefaultConfig() {
        return Map.of(
                "spring.datasource.url",
                "jdbc:mysql://{{ .Values.global.hosts.mysql }}:{{ .Values.global.ports.mysql }}/{{ .Values.mysql.database }}"
        );
    }

    public Map<String, Object> getValuesEntries(HelmContext context) {
        return Map.of("mysql", Map.of(
                        "enabled", true,
                        "database", "app",
                        "fullnameOverride", context.getAppName() + "-mysql",
                        "nameOverride", context.getAppName() + "-mysql",
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
                        Map.of("username", "mysql", "password", "mysql", "rootPassword", "mysql")),
                "global",
                Map.of("hosts", Map.of("mysql", context.getAppName() + "-mysql"), "ports", Map.of("mysql", 3306))
        );
    }

    public Map<String, String> getPreferredChart() {
        return Map.of(
                "name", "mysql",
                "version", "9.12.5",
                "repository", "https://charts.bitnami.com/bitnami"
        );
    }

    @Override
    public String dependencyName() {
        return "mysql";
    }
}
