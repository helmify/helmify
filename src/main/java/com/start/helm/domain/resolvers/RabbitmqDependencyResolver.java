package com.start.helm.domain.resolvers;

import com.start.helm.domain.helm.HelmContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.start.helm.util.HelmUtil.makeSecretKeyRef;

/**
 * Resolver for spring rabbitmq dependency.
 */
@Slf4j
@Component
public class RabbitmqDependencyResolver implements DependencyResolver {

	@Override
	public String dependencyName() {
		return "rabbitmq";
	}

	@Override
	public List<String> matchOn() {
		return List.of("spring-boot-starter-amqp", "spring-cloud-starter-stream-rabbit");
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of("rabbitmq-username", "{{ .Values.rabbitmq.auth.username | b64enc | quote }}", "rabbitmq-password",
				"{{ .Values.rabbitmq.auth.password | b64enc | quote }}");
	}

	public Map<String, Object> getValuesEntries(HelmContext context) {
		return Map.of("rabbitmq",
				Map.of("enabled", true, "port", 5672, "vhost", "/", "nameOverride", context.getAppName() + "-rabbitmq",
						"fullnameOverride", context.getAppName() + "-rabbitmq", "auth",
						Map.of("username", "guest", "password", "guest")),
				"global", Map.of("hosts", Map.of("rabbitmq", context.getAppName() + "-rabbitmq"), "ports",
						Map.of("rabbitmq", 5672)));
	}

	public Map<String, String> getPreferredChart() {
		return Map.of("name", "rabbitmq", "version", "11.9.0", "repository", "https://charts.bitnami.com/bitnami");
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of("spring.rabbitmq.host", "{{ .Values.global.hosts.rabbitmq }}", "spring.rabbitmq.port",
				"{{ .Values.global.ports.rabbitmq }}", "spring.rabbitmq.virtual-host", "{{ .Values.rabbitmq.vhost }}");
	}

	public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
		return List.of(makeSecretKeyRef("SPRING_RABBITMQ_USERNAME", "rabbitmq-username", context.getAppName()),
				makeSecretKeyRef("SPRING_RABBITMQ_PASSWORD", "rabbitmq-password", context.getAppName()));
	}

}
