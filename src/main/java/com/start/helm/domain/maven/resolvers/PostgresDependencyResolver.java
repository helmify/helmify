package com.start.helm.domain.maven.resolvers;

import static com.start.helm.HelmUtil.makeSecretKeyRef;

import com.start.helm.domain.helm.HelmChartFragment;
import com.start.helm.domain.helm.HelmContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class PostgresDependencyResolver implements DependencyResolver {

  @Override
  public List<String> matchOn() {
    return List.of("postgres");
  }

  @Override
  public Optional<HelmChartFragment> resolveDependency(HelmContext context) {
    HelmChartFragment fragment = new HelmChartFragment();

    fragment.setPreferredChart(getPreferredChart());
    fragment.setValuesEntries(getValuesEntries(context));
    fragment.setInitContainer(initContainer(context));
    fragment.setDefaultConfig(getDefaultConfig());
    fragment.setSecretEntries(getSecretEntries());
    fragment.setEnvironmentEntries(getEnvironmentEntries(context));

    return Optional.of(fragment);
  }

  private List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
    return List.of(
        makeSecretKeyRef("SPRING_DATASOURCE_USERNAME", "postgres-username", context.getAppName()),
        makeSecretKeyRef("SPRING_DATASOURCE_PASSWORD", "postgres-password", context.getAppName())
    );
  }


  private Map<String, Object> getSecretEntries() {
    return Map.of(
        "postgres-username", "{{ .Values.postgresql.auth.username | b64enc | quote }}"
        , "postgres-password", "{{ .Values.postgresql.auth.password | b64enc | quote }}"
    );
  }


  private Map<String, String> getDefaultConfig() {
    return Map.of(
        "spring.datasource.url",
        "jdbc:postgresql://{{ .Values.global.hosts.postgresql }}:{{ .Values.global.ports.postgresql }}/{{ .Values.postgresql.database }}"
    );
  }

  private Map<String, Object> getValuesEntries(HelmContext context) {
    return Map.of("postgresql", Map.of(
            "enabled", true,
            "database", "postgres",
            "fullnameOverride", "postgres",
            "nameOverride", "postgres",
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
        "global", Map.of("hosts", Map.of("postgresql", "postgresql"), "ports", Map.of("postgresql", 5432))
    );
  }

  private Map<String, String> getPreferredChart() {
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