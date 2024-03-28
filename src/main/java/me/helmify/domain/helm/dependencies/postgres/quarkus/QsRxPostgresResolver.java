package me.helmify.domain.helm.dependencies.postgres.quarkus;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.postgres.PostgresResolver;
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
				HelmUtil.makeSecretKeyRef("QUARKUS_DATASOURCE_USERNAME", "QUARKUS_DATASOURCE_USERNAME",
						context.getAppName()),
				HelmUtil.makeSecretKeyRef("QUARKUS_DATASOURCE_PASSWORD", "QUARKUS_DATASOURCE_PASSWORD",
						context.getAppName()));
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of("QUARKUS_DATASOURCE_USERNAME", "{{ .Values.postgresql.auth.username | b64enc | quote }}",
				"QUARKUS_DATASOURCE_PASSWORD", "{{ .Values.postgresql.auth.password | b64enc | quote }}");
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
