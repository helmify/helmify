package com.start.helm.domain.resolvers;

import com.start.helm.domain.helm.HelmContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Resolver for spring kafka dependency.
 * <a href="https://github.com/bitnami/charts/tree/main/bitnami/kafka">Also see Bitnami
 * Chart</a>
 */
@Slf4j
@Component
public class KafkaDependencyResolver implements DependencyResolver {

	//@formatter:off

	@Override
	public String dependencyName() {
		return "kafka";
	}

	@Override
	public List<String> matchOn() {
		return List.of("kafka-streams", "spring-cloud-starter-stream-kafka", "spring-kafka");
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of(
		);
	}

	private int getKafkaPort() {
		return 9092;
	}

	public Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of(
				"kafka",
				Map.of("enabled", true,
						"port", getKafkaPort() ,
						"nameOverride", context.getAppName() + "-kafka",
						"fullnameOverride", context.getAppName() + "-kafka",
						"listeners", Map.of(
								"client", Map.of(
									"protocol", "PLAINTEXT", // Allowed values are 'PLAINTEXT', 'SASL_PLAINTEXT', 'SASL_SSL' and 'SSL'
										"sslClientAuth", "none" // Allowed values are 'none', 'required' and 'requested'
								)
						)
				),
				"global", Map.of("hosts", Map.of("kafka", context.getAppName() + "-kafka"), "ports", Map.of("kafka", getKafkaPort() ))
		);
	}

	public Map<String, String> getPreferredChart() {
		return Map.of(
				"name", "kafka",
				"version", "26.4.2",
				"repository", "https://charts.bitnami.com/bitnami"
		);
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of(
				"spring.kafka.bootstrap-servers", "{{ .Values.global.hosts.kafka }}:{{ .Values.global.ports.kafka }}"
		);
	}

	public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
		return List.of(
		);
	}

}
