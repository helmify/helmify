package com.start.helm.domain.resolvers.mysql.quarkus;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.resolvers.mysql.MySqlResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.start.helm.util.HelmUtil.makeSecretKeyRef;

@Component
public class QsMySqlResolver implements MySqlResolver {

	@Override
	public List<String> matchOn() {
		return List.of("quarkus-jdbc-mysql");
	}

	public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
		return List.of(makeSecretKeyRef("QUARKUS_DATASOURCE_USERNAME", "mysql-username", context.getAppName()),
				makeSecretKeyRef("QUARKUS_DATASOURCE_PASSWORD", "mysql-password", context.getAppName()));
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of("mysql-username", "{{ .Values.mysql.auth.username | b64enc | quote }}", "mysql-password",
				"{{ .Values.mysql.auth.password | b64enc | quote }}");
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of("quarkus.datasource.jdbc.url",
				"jdbc:mysql://{{ .Values.global.hosts.mysql }}:{{ .Values.global.ports.mysql }}/{{ .Values.mysql.database }}");
	}

}
