package com.start.helm.domain.resolvers.postgres.quarkus;

import com.start.helm.domain.FrameworkVendor;
import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.resolvers.postgres.PostgresResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.start.helm.util.HelmUtil.makeSecretKeyRef;

@Component
public class QsPostgresResolver implements PostgresResolver {

	@Override
	public List<String> matchOn() {
		return List.of("quarkus-jdbc-postgresql");
	}

	public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
		return List.of(makeSecretKeyRef("QUARKUS_DATASOURCE_USERNAME", "postgres-username", context.getAppName()),
				makeSecretKeyRef("QUARKUS_DATASOURCE_PASSWORD", "postgres-password", context.getAppName()));
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of("postgres-username", "{{ .Values.postgresql.auth.username | b64enc | quote }}",
				"postgres-password", "{{ .Values.postgresql.auth.password | b64enc | quote }}");
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of("quarkus.datasource.url",
				"jdbc:postgresql://{{ .Values.global.hosts.postgresql }}:{{ .Values.global.ports.postgresql }}/{{ .Values.postgresql.database }}");
	}

	@Override
	public FrameworkVendor getVendor() {
		return FrameworkVendor.Quarkus;
	}

}
