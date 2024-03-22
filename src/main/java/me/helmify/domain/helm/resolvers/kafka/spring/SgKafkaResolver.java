package me.helmify.domain.helm.resolvers.kafka.spring;

import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.helm.resolvers.kafka.KafkaResolver;
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
public class SgKafkaResolver implements KafkaResolver {

	@Override
	public List<String> matchOn() {
		return List.of("kafka-streams", "spring-cloud-starter-stream-kafka", "spring-kafka");
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of("spring.kafka.bootstrap-servers",
				"{{ .Values.global.hosts.kafka }}:{{ .Values.global.ports.kafka }}");
	}

}
