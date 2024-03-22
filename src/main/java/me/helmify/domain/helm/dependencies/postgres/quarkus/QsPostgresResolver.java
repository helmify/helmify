package me.helmify.domain.helm.dependencies.postgres.quarkus;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.postgres.PostgresResolver;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class QsPostgresResolver implements PostgresResolver {

	@Override
	public List<String> matchOn() {
		return List.of("quarkus-jdbc-postgresql");
	}

	public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
		return List.of(
				HelmUtil.makeSecretKeyRef("QUARKUS_DATASOURCE_USERNAME", "postgresql-username", context.getAppName()),
				HelmUtil.makeSecretKeyRef("QUARKUS_DATASOURCE_PASSWORD", "postgresql-password", context.getAppName()));
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of("postgresql-username", "{{ .Values.postgresql.auth.username | b64enc | quote }}",
				"postgresql-password", "{{ .Values.postgresql.auth.password | b64enc | quote }}");
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of("%prod.quarkus.datasource.jdbc.url",
				"jdbc:postgresql://{{ .Values.global.hosts.postgresql }}:{{ .Values.global.ports.postgresql }}/{{ .Values.postgresql.database }}",
				"%prod.quarkus.datasource.db-kind", "postgresql");
	}

	@Override
	public FrameworkVendor getVendor() {
		return FrameworkVendor.Quarkus;
	}

}
