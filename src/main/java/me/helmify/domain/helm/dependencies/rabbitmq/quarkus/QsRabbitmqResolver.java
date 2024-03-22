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
		return List.of("quarkus-smallrye-reactive-messaging-rabbitmq");
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of(
				"rabbitmq-username", "{{ .Values.rabbitmq.auth.username | b64enc | quote }}"
				, "rabbitmq-password", "{{ .Values.rabbitmq.auth.password | b64enc | quote }}"
		);
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of(
				"%prod.rabbitmq-host", "{{ .Values.global.hosts.rabbitmq }}",
				"%prod.rabbitmq-port", "{{ .Values.global.ports.rabbitmq }}",
				"%prod.rabbitmq-virtual-host", "{{ .Values.rabbitmq.vhost }}"
		);
	}

	public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
		return List.of(
				makeSecretKeyRef("RABBITMQ_USERNAME", "rabbitmq-username", context.getAppName()),
				makeSecretKeyRef("RABBITMQ_PASSWORD", "rabbitmq-password", context.getAppName())
		);
	}
	@Override
	public FrameworkVendor getVendor() {
		return FrameworkVendor.Quarkus;
	}

}
