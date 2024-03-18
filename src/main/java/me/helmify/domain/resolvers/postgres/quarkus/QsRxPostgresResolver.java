package me.helmify.domain.resolvers.postgres.quarkus;

import me.helmify.domain.FrameworkVendor;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.resolvers.postgres.PostgresResolver;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class QsRxPostgresResolver implements PostgresResolver {

	@Override
	public List<String> matchOn() {
		return List.of("quarkus-reactive-pg-client");
	}

	public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
		return List.of(
				HelmUtil.makeSecretKeyRef("QUARKUS_DATASOURCE_USERNAME", "postgres-username", context.getAppName()),
				HelmUtil.makeSecretKeyRef("QUARKUS_DATASOURCE_PASSWORD", "postgres-password", context.getAppName()));
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of("postgres-username", "{{ .Values.postgresql.auth.username | b64enc | quote }}",
				"postgres-password", "{{ .Values.postgresql.auth.password | b64enc | quote }}");
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of("%prod.quarkus.datasource.reactive.url",
				"vertx-reactive:postgresql://{{ .Values.global.hosts.postgresql }}:{{ .Values.global.ports.postgresql }}/{{ .Values.postgresql.database }}");
	}

	@Override
	public FrameworkVendor getVendor() {
		return FrameworkVendor.Quarkus;
	}

}
