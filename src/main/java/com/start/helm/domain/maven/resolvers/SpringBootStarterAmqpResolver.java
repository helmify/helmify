package com.start.helm.domain.maven.resolvers;

import static com.start.helm.HelmUtil.makeSecretKeyRef;

import com.start.helm.domain.dependency.DependencyFetcher;
import com.start.helm.domain.helm.HelmChartFragment;
import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.HelmDependency;
import com.start.helm.domain.helm.chart.model.InitContainer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public
class SpringBootStarterAmqpResolver implements DependencyResoler {


  @Override
  public List<String> matchOn() {
    return List.of("spring-boot-starter-amqp", "spring-cloud-starter-stream-rabbit");
  }

  @Override
  public Optional<HelmChartFragment> resolveDependency(HelmContext context) {
    Map<String, String> preferredChart = getPreferredChart();
    context.addHelmDependency(
        new HelmDependency(preferredChart.get("name"), preferredChart.get("version"), preferredChart.get("repository"),
            List.of()));

    HelmChartFragment fragment = new HelmChartFragment();
    fragment.setEnvironmentEntries(getEnvironmentEntries(context));
    fragment.setDefaultConfig(getDefaultConfig());
    fragment.setPreferredChart(getPreferredChart());
    fragment.setValuesEntries(getValuesEntries());


    String dependencyName = "rabbitmq";
    String name = "\"{{ include \"%s.fullname\" . }}-%schecker\"".formatted(context.getAppName() , dependencyName);

    fragment.setInitContainer(Map.of(
        "name", name,
        "image", "docker.io/busybox:stable",
        "imagePullPolicy", "Always",
        "securityContext", Map.of(
            "allowPrivilegeEscalation", false,
            "runAsUser", 1000,
            "runAsGroup", 1000,
            "runAsNonRoot", true
        ),
        "command",  List.of(
            "sh",
            "-c",
            """
            echo 'Waiting for %s to become ready...'
            until printf "." && nc -z -w 2 {{ .Values.global.hosts.%s }} {{ .Values.global.ports.%s }}; do
                sleep 2;
            done;
            echo '%s OK ✓'
            """.formatted(dependencyName, dependencyName, dependencyName, dependencyName)
        ),
        "resources", Map.of(
            "requests", Map.of(
                "cpu", "20m",
                "memory", "32Mi"
            ),
            "limits", Map.of(
                "cpu", "20m",
                "memory", "32Mi"
            )
        )
    ));

    return Optional.of(fragment);
  }

  private Map<String, Object> getValuesEntries() {
    return Map.of("rabbitmq",
        Map.of( "enabled", true,
                "port", 5672,
                "virtual-host", "/")
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
        "spring.rabbitmq.port", "{{ .Values.rabbitmq.port }}",
        "spring.rabbitmq.virtual-host", "{{ .Values.rabbitmq.virtual-host }}"
    );
  }

  private List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
    return List.of(
        makeSecretKeyRef("SPRING_RABBITMQ_USERNAME", "rabbitmq-username", context.getAppName()),
        makeSecretKeyRef("SPRING_RABBITMQ_PASSWORD", "rabbitmq-password", context.getAppName())
    );
  }


}
