package me.helmify.domain.helm.dependencies.mysql.spring;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.mysql.MySqlResolver;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Resolver for spring postgres dependency.
 */
@Component
public class SgMySqlResolver implements MySqlResolver {

	//@formatter:off

	@Override
	public List<String> matchOn() {
		return List.of("mysql-connector-j");
	}


	public Map<String, Object> getSecretEntries() {
		return Map.of(
				"SPRING_DATASOURCE_USERNAME", "{{ .Values.mysql.auth.username | b64enc | quote }}"
				, "SPRING_DATASOURCE_PASSWORD", "{{ .Values.mysql.auth.password | b64enc | quote }}"
		);
	}


	public Map<String, String> getDefaultConfig() {
		return Map.of(
				"SPRING_DATASOURCE_URL",
				"jdbc:mysql://{{ .Values.global.hosts.mysql }}:{{ .Values.global.ports.mysql }}/{{ .Values.mysql.database }}"
		);
	}


}
