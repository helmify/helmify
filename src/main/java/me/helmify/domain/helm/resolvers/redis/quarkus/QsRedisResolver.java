package me.helmify.domain.helm.resolvers.redis.quarkus;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.resolvers.redis.RedisResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class QsRedisResolver implements RedisResolver {

	@Override
	public List<String> matchOn() {
		return List.of("quarkus-redis-client");
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of("%prod.quarkus.redis.hosts",
				"redis://{{ .Values.global.hosts.redis }}:{{ .Values.global.ports.redis }}");
	}

	@Override
	public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
		String appName = context.getAppName();
		return List.of(Map.of("name", "QUARKUS_REDIS_PASSWORD", "valueFrom", Map.of("secretKeyRef", Map.of("name",
				"REPLACEME-redis".replace("REPLACEME", appName), "key", "redis-password", "optional", false))));
	}

}
