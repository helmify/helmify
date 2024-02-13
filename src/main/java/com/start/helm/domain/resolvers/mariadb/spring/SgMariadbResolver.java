package com.start.helm.domain.resolvers.mariadb.spring;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.resolvers.mariadb.MariaDbResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.start.helm.util.HelmUtil.makeSecretKeyRef;

/**
 * Resolver for spring mariadb dependency.
 */
@Component
public class SgMariadbResolver implements MariaDbResolver {

	//@formatter:off

	@Override
	public List<String> matchOn() {
		return List.of("mariadb-java-client");
	}


	public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
		return List.of(
				makeSecretKeyRef("SPRING_DATASOURCE_USERNAME", "mariadb-username", context.getAppName()),
				makeSecretKeyRef("SPRING_DATASOURCE_PASSWORD", "mariadb-password", context.getAppName())
		);
	}


	public Map<String, Object> getSecretEntries() {
		return Map.of(
				"mariadb-username", "{{ .Values.mariadb.auth.username | b64enc | quote }}"
				, "mariadb-password", "{{ .Values.mariadb.auth.password | b64enc | quote }}"
		);
	}


	public Map<String, String> getDefaultConfig() {
		return Map.of(
				"spring.datasource.url",
				"jdbc:mariadb://{{ .Values.global.hosts.mariadb }}:{{ .Values.global.ports.mariadb }}/{{ .Values.mariadb.database }}"
		);
	}

}
