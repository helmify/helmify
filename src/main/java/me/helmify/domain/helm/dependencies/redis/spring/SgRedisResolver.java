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
                "spring.data.redis.host", "{{ .Values.global.hosts.redis }}",
                "spring.data.redis.port", "{{ .Values.global.ports.redis }}");
    }

    @Override
    public Map<String,Object> getSecretEntries() {
        return Map.of(
                "SPRING_DATA_REDIS_PASSWORD", "{{ \"redis\" | b64enc | quote }}"
        );
    }

    @Override
    public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
        String appName = context.getAppName();
        return List.of(Map.of(
                "name", "SPRING_DATA_REDIS_PASSWORD",
                "valueFrom", Map.of(
                        "secretKeyRef", Map.of(
                                "name", "%s-secret".formatted( appName),
                                "key", "SPRING_DATA_REDIS_PASSWORD",
                                "optional", false))));
    }

}
