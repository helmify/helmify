package com.start.helm.domain.maven.resolvers;

import static com.start.helm.HelmUtil.initContainer;
import static com.start.helm.HelmUtil.makeSecretKeyRef;

import com.start.helm.domain.helm.HelmChartSlice;
import com.start.helm.domain.helm.HelmContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Resolver for spring rabbitmq dependency.
 */
@Slf4j
@Component
public class RabbitmqDependencyResolver implements DependencyResolver {

  @Override
  public String dependencyName() {
    return "rabbitmq";
  }

  @Override
  public List<String> matchOn() {
    return List.of("spring-boot-starter-amqp", "spring-cloud-starter-stream-rabbit");
  }

  /**
   * HelmChartSlice for RabbitMQ.
   */
  @Override
  public Optional<HelmChartSlice> resolveDependency(HelmContext context) {

    HelmChartSlice slice = new HelmChartSlice();
    slice.setEnvironmentEntries(getEnvironmentEntries(context));
    slice.setDefaultConfig(getDefaultConfig());
    slice.setPreferredChart(getPreferredChart());
    slice.setValuesEntries(getValuesEntries(context));
    slice.setSecretEntries(getSecretEntries());
    slice.setInitContainer(initContainer(context.getAppName(), dependencyName()));

    return Optional.of(slice);
  }

  private static Map<String, Object> getSecretEntries() {
    return Map.of(
        "rabbitmq-username", "{{ .Values.rabbitmq.auth.username | b64enc | quote }}"
        , "rabbitmq-password", "{{ .Values.rabbitmq.auth.password | b64enc | quote }}"
    );
  }

  private Map<String, Object> getValuesEntries(HelmContext context) {
    return Map.of(
        "rabbitmq",
        Map.of("enabled", true,
            "port", 5672,
            "vhost", "/",
            "nameOverride", context.getAppName() + "-rabbitmq",
            "fullnameOverride", context.getAppName() + "-rabbitmq",
            "auth", Map.of("username", "guest", "password", "guest")
        ),
        "global", Map.of("hosts", Map.of("rabbitmq", context.getAppName() + "-rabbitmq"), "ports", Map.of("rabbitmq", 5672))
    );
  }

  private Map<String, String> getPreferredChart() {
    return Map.of(
        "name", "rabbitmq",
        "version", "11.9.0",
        "repository", "https://charts.bitnami.com/bitnami"
    );
  }

  private Map<String, String> getDefaultConfig() {
    return Map.of(
        "spring.rabbitmq.host", "{{ .Values.global.hosts.rabbitmq }}",
        "spring.rabbitmq.port", "{{ .Values.global.ports.rabbitmq }}",
        "spring.rabbitmq.virtual-host", "{{ .Values.rabbitmq.vhost }}"
    );
  }

  private List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
    return List.of(
        makeSecretKeyRef("SPRING_RABBITMQ_USERNAME", "rabbitmq-username", context.getAppName()),
        makeSecretKeyRef("SPRING_RABBITMQ_PASSWORD", "rabbitmq-password", context.getAppName())
    );
  }


}
