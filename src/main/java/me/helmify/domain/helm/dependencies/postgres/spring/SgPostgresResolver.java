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

	public Map<String, Object> getSecretEntries() {
		return Map.of(
				"SPRING_DATASOURCE_USERNAME", "{{ .Values.postgresql.auth.username | b64enc | quote }}"
				, "SPRING_DATASOURCE_PASSWORD", "{{ .Values.postgresql.auth.password | b64enc | quote }}"
		);
	}


	public Map<String, String> getDefaultConfig() {
		return Map.of(
				"SPRING_DATASOURCE_URL",
				"jdbc:postgresql://{{ .Values.global.hosts.postgresql }}:{{ .Values.global.ports.postgresql }}/{{ .Values.postgresql.database }}"
		);
	}

}
