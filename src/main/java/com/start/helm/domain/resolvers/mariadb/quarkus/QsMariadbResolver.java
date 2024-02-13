package com.start.helm.domain.resolvers.mariadb.quarkus;

import com.start.helm.domain.FrameworkVendor;
import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.resolvers.mariadb.MariaDbResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.start.helm.util.HelmUtil.makeSecretKeyRef;

@Component
public class QsMariadbResolver implements MariaDbResolver {

	@Override
	public List<String> matchOn() {
		return List.of("quarkus-jdbc-mariadb");
	}

	public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
		return List.of(makeSecretKeyRef("QUARKUS_DATASOURCE_USERNAME", "mariadb-username", context.getAppName()),
				makeSecretKeyRef("QUARKUS_DATASOURCE_PASSWORD", "mariadb-password", context.getAppName()));
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of("mariadb-username", "{{ .Values.mariadb.auth.username | b64enc | quote }}", "mariadb-password",
				"{{ .Values.mariadb.auth.password | b64enc | quote }}");
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of("quarkus.datasource.jdbc.url",
				"jdbc:mariadb://{{ .Values.global.hosts.mariadb }}:{{ .Values.global.ports.mariadb }}/{{ .Values.mariadb.database }}",
				"quarkus.datasource.db-kind", "mariadb");
	}

	@Override
	public FrameworkVendor getVendor() {
		return FrameworkVendor.Quarkus;
	}

}
