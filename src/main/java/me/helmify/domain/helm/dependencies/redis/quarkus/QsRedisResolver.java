package me.helmify.domain.helm.dependencies.redis.quarkus;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.redis.RedisResolver;
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
		return Map.of("QUARKUS_REDIS_HOSTS",
				"redis://{{ .Values.global.hosts.redis }}:{{ .Values.global.ports.redis }}");
	}

	@Override
	public FrameworkVendor getVendor() {
		return FrameworkVendor.Quarkus;
	}

}
