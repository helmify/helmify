package com.start.helm.domain.resolvers;

import com.start.helm.domain.helm.HelmContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.start.helm.util.HelmUtil.makeSecretKeyRef;

/**
 * Resolver for spring mongodb dependency.
 */
@Component
public class MongodbDependencyResolver implements DependencyResolver {

	//@formatter:off

	@Override
	public List<String> matchOn() {
		return List.of("spring-boot-starter-data-mongodb", "spring-boot-starter-data-mongodb-reactive");
	}

	public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
		return List.of(
				makeSecretKeyRef("SPRING_DATA_MONGODB_USERNAME", "mongodb-username", context.getAppName()),
				makeSecretKeyRef("SPRING_DATA_MONGODB_PASSWORD", "mongodb-password", context.getAppName()));
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of(
				"mongodb-username", "{{ (first .Values.mongodb.auth.usernames) | b64enc | quote }}",
				"mongodb-password", "{{ (first .Values.mongodb.auth.passwords) | b64enc | quote }}");
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of(
				"spring.data.mongodb.uri",
				"mongodb://{{ .Values.global.hosts.mongodb }}:{{ .Values.global.ports.mongodb }}/{{ .Values.mongodb.database }}");
	}

	public Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of("mongodb", Map.of(
				"enabled", true,
				"database", "db",
				"fullnameOverride", context.getAppName() + "-mongodb",
				"nameOverride", context.getAppName() + "-mongodb",
				"architecture", "standalone",
				"primary", Map.of(
						"persistence", Map.of(
								"enabled", true,
								"storageClass", "",
								"accessModes", List.of("ReadWriteOnce"),
								"size", "1Gi")),
						"auth", Map.of(
								"usernames", List.of("mongodb"),
								"passwords", List.of("mongodb"),
								"rootPassword", "mongodb",
								"databases", List.of("db"))),
				"global", Map.of(
						"hosts", Map.of("mongodb", context.getAppName() + "-mongodb"),
						"ports", Map.of("mongodb", 27017)));
	}

	public Map<String, String> getPreferredChart() {
		return Map.of("name", "mongodb", "version", "14.0.11", "repository", "https://charts.bitnami.com/bitnami");
	}

	@Override
	public String dependencyName() {
		return "mongodb";
	}

}
