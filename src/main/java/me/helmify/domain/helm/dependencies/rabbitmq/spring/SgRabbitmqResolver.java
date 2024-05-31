package me.helmify.domain.helm.dependencies.rabbitmq.spring;

import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.rabbitmq.RabbitmqResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static me.helmify.util.HelmUtil.makeSecretKeyRef;

/**
 * Resolver for spring rabbitmq dependency.
 */
@Slf4j
@Component
public class SgRabbitmqResolver implements RabbitmqResolver {

	//@formatter:off

	@Override
	public List<String> matchOn() {
		return List.of("spring-boot-starter-amqp", "spring-cloud-starter-stream-rabbit");
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of(
				"SPRING_RABBITMQ_USERNAME", "{{ .Values.rabbitmq.auth.username | b64enc | quote }}"
				, "SPRING_RABBITMQ_PASSWORD", "{{ .Values.rabbitmq.auth.password | b64enc | quote }}"
		);
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of(
				"SPRING_RABBITMQ_HOST", "{{ .Values.global.hosts.rabbitmq }}",
				"SPRING_RABBITMQ_PORT", "{{ .Values.global.ports.rabbitmq }}",
				"SPRING_RABBITMQ_VIRTUAL-HOST", "{{ .Values.rabbitmq.vhost }}"
		);
	}

}
