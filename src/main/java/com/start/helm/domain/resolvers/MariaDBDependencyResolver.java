package com.start.helm.domain.resolvers;

import com.start.helm.domain.helm.HelmContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.start.helm.util.HelmUtil.makeSecretKeyRef;

/**
 * Resolver for spring mariadb dependency.
 */
@Component
public class MariaDBDependencyResolver implements DependencyResolver {

	//@formatter:off

	@Override
	public List<String> matchOn() {
		return List.of("mariadb-java-client");
	}

	public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
		return List.of(

				makeSecretKeyRef("SPRING_DATASOURCE_USERNAME", "mariadb-username", context.getAppName()),
				makeSecretKeyRef("SPRING_DATASOURCE_PASSWORD", "mariadb-password", context.getAppName()));
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of(
				"mariadb-username", "{{ .Values.mariadb.auth.username | b64enc | quote }}",
				"mariadb-password", "{{ .Values.mariadb.auth.password | b64enc | quote }}");
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of(
				"spring.datasource.url",
				"jdbc:mariadb://{{ .Values.global.hosts.mariadb }}:{{ .Values.global.ports.mariadb }}/{{ .Values.mariadb.database }}");
	}

	public Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of("mariadb",
				Map.of("enabled", true,
						"database", "my_database",
						"fullnameOverride", context.getAppName() + "-mariadb",
						"nameOverride", context.getAppName() + "-mariadb",
						"architecture", "standalone",
						"primary", Map.of(
								"persistence", Map.of(
										"enabled", true,
										"storageClass", "",
										"accessModes", List.of("ReadWriteOnce"),
										"size", "1Gi")),
						"auth", Map.of(
								"username", "mariadb",
								"password", "mariadb",
								"rootPassword", "mariadb")),
				"global", Map.of(
						"hosts", Map.of(
								"mariadb", context.getAppName() + "-mariadb"),
						"ports", Map.of("mariadb", 3306)));
	}

	public Map<String, String> getPreferredChart() {
		return Map.of("name", "mariadb", "version", "14.0.3", "repository", "https://charts.bitnami.com/bitnami");
	}

	@Override
	public String dependencyName() {
		return "mariadb";
	}

}
