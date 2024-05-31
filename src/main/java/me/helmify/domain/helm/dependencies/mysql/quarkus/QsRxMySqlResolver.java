package me.helmify.domain.helm.dependencies.mysql.quarkus;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.mysql.MySqlResolver;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class QsRxMySqlResolver implements MySqlResolver {

	@Override
	public List<String> matchOn() {
		return List.of("quarkus-reactive-mysql-client");
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of("QUARKUS_DATASOURCE_USERNAME", "{{ .Values.mysql.auth.username | b64enc | quote }}",
				"QUARKUS_DATASOURCE_PASSWORD", "{{ .Values.mysql.auth.password | b64enc | quote }}");
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of("QUARKUS_DATASOURCE_REACTIVE_URL",
				"vertx-reactive:mysql://{{ .Values.global.hosts.mysql }}:{{ .Values.global.ports.mysql }}/{{ .Values.mysql.database }}");
	}

	@Override
	public FrameworkVendor getVendor() {
		return FrameworkVendor.Quarkus;
	}

}
