package me.helmify.domain.resolvers.kafka.quarkus;

import me.helmify.domain.resolvers.kafka.KafkaResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class QsKafkaResolver implements KafkaResolver {

	@Override
	public List<String> matchOn() {
		return List.of("quarkus-smallrye-reactive-messaging-kafka");
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of("mp.messaging.incoming.kafka.bootstrap.servers",
				"{{ .Values.global.hosts.kafka }}:{{ .Values.global.ports.kafka }}");
	}

}
