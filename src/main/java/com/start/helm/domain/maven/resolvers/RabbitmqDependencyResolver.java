package com.start.helm.domain.maven.resolvers;

import static com.start.helm.HelmUtil.makeSecretKeyRef;

import com.start.helm.domain.helm.HelmChartFragment;
import com.start.helm.domain.helm.HelmContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

  @Override
  public Optional<HelmChartFragment> resolveDependency(HelmContext context) {

    HelmChartFragment fragment = new HelmChartFragment();
    fragment.setEnvironmentEntries(getEnvironmentEntries(context));
    fragment.setDefaultConfig(getDefaultConfig());
    fragment.setPreferredChart(getPreferredChart());
    fragment.setValuesEntries(getValuesEntries());
    fragment.setSecretEntries(getSecretEntries());
    fragment.setInitContainer(initContainer(context));

    return Optional.of(fragment);
  }

  private static Map<String, Object> getSecretEntries() {
    return Map.of(
        "rabbitmq-username", "{{ .Values.rabbitmq.auth.username | b64enc | quote }}"
        , "rabbitmq-password", "{{ .Values.rabbitmq.auth.password | b64enc | quote }}"
    );
  }

  private Map<String, Object> getValuesEntries() {
    return Map.of(
        "rabbitmq",
        Map.of("enabled", true,
            "port", 5672,
            "vhost", "/",
            "auth", Map.of("username", "guest", "password", "guest")
        ),
        "global", Map.of("hosts", Map.of("rabbitmq", "rabbitmq"), "ports", Map.of("rabbitmq", 5672))
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
