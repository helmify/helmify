package com.start.helm;

import java.util.Map;

public final class HelmUtil {

  public static Map<String, Object> makeSecretKeyRef(String name, String key, String appName) {
    return Map.of("name", name, "valueFrom", Map.of(
        "secretKeyRef", Map.of(
            "name", "{{ include \"REPLACEME.fullname\" . }}".replace("REPLACEME", appName),
            "key", key,
            "optional", false
        )
    ));
  }

}
