package com.start.helm.domain.resolvers;

import com.start.helm.domain.helm.HelmContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Resolver for redis dependency.
 */
@Slf4j
@Component
public class RedisDependencyResolver implements DependencyResolver {

  @Override
  public String dependencyName() {
    return "redis";
  }

  @Override
  public List<String> matchOn() {
    return List.of("spring-boot-starter-data-redis", "spring-data-redis");
  }


  public Map<String, Object> getValuesEntries(HelmContext context) {
    return Map.of(
            "redis",
            Map.of("enabled", true,
                    "port", 6379,
                    "nameOverride", context.getAppName() + "-redis",
                    "fullnameOverride", context.getAppName() + "-redis",
                    "architecture", "standalone"
            ),
            "global", Map.of("hosts", Map.of("redis", getRedisHost(context)), "ports", Map.of("redis", getRedisPort()))
    );
  }

  private int getRedisPort() {
    return 6379;
  }

  private String getRedisHost(HelmContext context) {
    return context.getAppName() + "-redis-master";
  }

  public Map<String, String> getPreferredChart() {
    return Map.of(
            "name", "redis",
            "version", "18.1.2",
            "repository", "https://charts.bitnami.com/bitnami"
    );
  }

  public Map<String, String> getDefaultConfig() {
    return Map.of(
            "spring.data.redis.host", "{{ .Values.global.hosts.redis }}",
            "spring.data.redis.port", "{{ .Values.global.ports.redis }}"
    );
  }

  @Override
  public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
    String appName = context.getAppName();
    Map<String, Object> redisPasswordRef = Map.of("name", "SPRING_DATA_REDIS_PASSWORD", "valueFrom", Map.of(
            "secretKeyRef", Map.of(
                    "name", "REPLACEME-redis".replace("REPLACEME", appName),
                    "key", "redis-password",
                    "optional", false
            )
    ));
    return List.of(redisPasswordRef);
  }
}
