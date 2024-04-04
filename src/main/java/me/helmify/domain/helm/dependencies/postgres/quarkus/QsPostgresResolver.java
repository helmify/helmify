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

	public Map<String, Object> getSecretEntries() {
		return Map.of("QUARKUS_DATASOURCE_USERNAME", "{{ .Values.postgresql.auth.username | b64enc | quote }}",
				"QUARKUS_DATASOURCE_PASSWORD", "{{ .Values.postgresql.auth.password | b64enc | quote }}");
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of("QUARKUS_DATASOURCE_JDBC_URL",
				"jdbc:postgresql://{{ .Values.global.hosts.postgresql }}:{{ .Values.global.ports.postgresql }}/{{ .Values.postgresql.database }}",
				"QUARKUS_DATASOURCE_DB-KIND", "postgresql");
	}

	@Override
	public FrameworkVendor getVendor() {
		return FrameworkVendor.Quarkus;
	}

}
