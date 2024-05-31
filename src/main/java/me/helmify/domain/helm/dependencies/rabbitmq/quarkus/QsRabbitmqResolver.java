package me.helmify.domain.helm.dependencies.rabbitmq.quarkus;

import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.rabbitmq.RabbitmqResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static me.helmify.util.HelmUtil.makeSecretKeyRef;

/**
 * Resolver for quarkus rabbitmq dependency.
 */
@Slf4j
@Component
public class QsRabbitmqResolver implements RabbitmqResolver {

	//@formatter:off

	@Override
	public List<String> matchOn() {
		return List.of("quarkus-smallrye-reactive-messaging-rabbitmq", "quarkus-messaging-rabbitmq");
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of(
				"MP_MESSAGING_CONNECTOR_SMALLRYE-RABBITMQ_USERNAME", "{{ .Values.rabbitmq.auth.username | b64enc | quote }}"
				, "MP_MESSAGING_CONNECTOR_SMALLRYE-RABBITMQ_PASSWORD", "{{ .Values.rabbitmq.auth.password | b64enc | quote }}"
		);
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of(
				"MP_MESSAGING_CONNECTOR_SMALLRYE-RABBITMQ_HOST", "{{ .Values.global.hosts.rabbitmq }}",
				"MP_MESSAGING_CONNECTOR_SMALLRYE-RABBITMQ_PORT", "{{ .Values.global.ports.rabbitmq }}",
				"MP_MESSAGING_CONNECTOR_SMALLRYE-RABBITMQ_VIRTUAL-HOST", "{{ .Values.rabbitmq.vhost }}"
		);
	}

	@Override
	public FrameworkVendor getVendor() {
		return FrameworkVendor.Quarkus;
	}

}
