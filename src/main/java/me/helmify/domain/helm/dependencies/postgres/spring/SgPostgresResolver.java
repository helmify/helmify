package me.helmify.domain.helm.dependencies.postgres.spring;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.postgres.PostgresResolver;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
				HelmUtil.makeSecretKeyRef("SPRING_DATASOURCE_USERNAME", "postgres-username", context.getAppName()),
				HelmUtil.makeSecretKeyRef("SPRING_DATASOURCE_PASSWORD", "postgres-password", context.getAppName())
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
