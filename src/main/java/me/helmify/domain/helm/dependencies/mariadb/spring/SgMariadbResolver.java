package me.helmify.domain.helm.dependencies.mariadb.spring;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.mariadb.MariaDbResolver;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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


	public Map<String, Object> getSecretEntries() {
		return Map.of(
				"SPRING_DATASOURCE_USERNAME", "{{ .Values.mariadb.auth.username | b64enc | quote }}"
				, "SPRING_DATASOURCE_PASSWORD", "{{ .Values.mariadb.auth.password | b64enc | quote }}"
		);
	}


	public Map<String, String> getDefaultConfig() {
		return Map.of(
				"SPRING_DATASOURCE_URL",
				"jdbc:mariadb://{{ .Values.global.hosts.mariadb }}:{{ .Values.global.ports.mariadb }}/{{ .Values.mariadb.database }}"
		);
	}

}
