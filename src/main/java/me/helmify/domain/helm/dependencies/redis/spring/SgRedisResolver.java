package me.helmify.domain.helm.dependencies.redis.spring;

import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.redis.RedisResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Resolver for redis dependency.
 */
@Slf4j
@Component
public class SgRedisResolver implements RedisResolver {

	//@formatter:off

    @Override
    public List<String> matchOn() {
        return List.of("spring-boot-starter-data-redis", "spring-data-redis");
    }



    public Map<String, String> getDefaultConfig() {
        return Map.of(
                "SPRING_DATA_REDIS_HOST", "{{ .Values.global.hosts.redis }}",
                "SPRING_DATA_REDIS_PORT", "{{ .Values.global.ports.redis }}");
    }

    @Override
    public Map<String,Object> getSecretEntries() {
        return Map.of(
                "SPRING_DATA_REDIS_PASSWORD", "{{ .Values.redis.auth.password | b64enc | quote }}"
        );
    }

}
