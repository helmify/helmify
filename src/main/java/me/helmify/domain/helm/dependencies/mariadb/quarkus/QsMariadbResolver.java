package me.helmify.domain.helm.dependencies.mariadb.quarkus;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.mariadb.MariaDbResolver;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class QsMariadbResolver implements MariaDbResolver {

	@Override
	public List<String> matchOn() {
		return List.of("quarkus-jdbc-mariadb");
	}

	public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
		return List.of(
				HelmUtil.makeSecretKeyRef("QUARKUS_DATASOURCE_USERNAME", "QUARKUS_DATASOURCE_USERNAME",
						context.getAppName()),
				HelmUtil.makeSecretKeyRef("QUARKUS_DATASOURCE_PASSWORD", "QUARKUS_DATASOURCE_PASSWORD",
						context.getAppName()));
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of("QUARKUS_DATASOURCE_USERNAME", "{{ .Values.mariadb.auth.username | b64enc | quote }}",
				"QUARKUS_DATASOURCE_PASSWORD", "{{ .Values.mariadb.auth.password | b64enc | quote }}");
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of("%prod.quarkus.datasource.jdbc.url",
				"jdbc:mariadb://{{ .Values.global.hosts.mariadb }}:{{ .Values.global.ports.mariadb }}/{{ .Values.mariadb.database }}",
				"%prod.quarkus.datasource.db-kind", "mariadb");
	}

	@Override
	public FrameworkVendor getVendor() {
		return FrameworkVendor.Quarkus;
	}

}
