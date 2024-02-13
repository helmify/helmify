package com.start.helm.domain.resolvers.postgres.spring;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.resolvers.postgres.PostgresResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.start.helm.util.HelmUtil.makeSecretKeyRef;

/**
 * Resolver for spring postgres dependency.
 */
@Component
public class SgPostgresResolver implements PostgresResolver {

	//@formatter:off

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

}
