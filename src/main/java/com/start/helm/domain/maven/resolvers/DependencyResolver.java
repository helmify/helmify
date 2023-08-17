package com.start.helm.domain.maven.resolvers;

import com.start.helm.domain.helm.HelmChartFragment;
import com.start.helm.domain.helm.HelmContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DependencyResolver {

  List<String> matchOn();

  default boolean matches(String artifactId) {
    return matchOn().stream().anyMatch(artifactId::contains);
  }

  Optional<HelmChartFragment> resolveDependency(HelmContext context);

  String dependencyName();

  default Map<String, Object> initContainer(HelmContext context) {
    String name = "\"{{ include \"%s.fullname\" . }}-%schecker\"".formatted(context.getAppName(), dependencyName());
    return Map.of(
        "name", name,
        "image", "docker.io/busybox:stable",
        "imagePullPolicy", "Always",
        "securityContext", Map.of(
            "allowPrivilegeEscalation", false,
            "runAsUser", 1000,
            "runAsGroup", 1000,
            "runAsNonRoot", true
        ),
        "command", List.of(
            "sh",
            "-c",
            """
                echo 'Waiting for %s to become ready...'
                until printf "." && nc -z -w 2 {{ .Values.global.hosts.%s }} {{ .Values.global.ports.%s }}; do
                    sleep 2;
                done;
                echo '%s OK ✓'
                """.formatted(dependencyName(), dependencyName(), dependencyName(), dependencyName())
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
    );
  }

  ;

}
