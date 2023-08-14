package com.start.helm.domain.helm.chart.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InitContainer {
  String name;
  String image;
  String imagePullPolicy;
  Map<String, Object> securityContext;
  List<String> command;
  Map<String, Object> resources;

    public static InitContainer prepare(String appName, String dependencyName) {

      String nameTemplate = "\"{{ include \"%s.fullname\" . }}-%schecker\"";

      return new InitContainer(
          String.format(nameTemplate, appName, dependencyName),
          "docker.io/busybox:stable",
            "Always",
            Map.of(
                "allowPrivilegeEscalation", false,
                "runAsUser", 1000,
                "runAsGroup", 1000,
                "runAsNonRoot", true
            ),
            List.of(
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
            Map.of(
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
  }
