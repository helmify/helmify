package me.helmify.domain.helm.dependencies.mongodb.spring;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.mongodb.MongodbResolver;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SgMongodbResolver implements MongodbResolver {

	//@formatter:off

    @Override
    public List<String> matchOn() {
        return List.of("spring-boot-starter-data-mongodb", "spring-boot-starter-data-mongodb-reactive");
    }

    public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
        return List.of(
                HelmUtil.makeSecretKeyRef("SPRING_DATA_MONGODB_USERNAME", "SPRING_DATA_MONGODB_USERNAME", context.getAppName()),
                HelmUtil.makeSecretKeyRef("SPRING_DATA_MONGODB_PASSWORD", "SPRING_DATA_MONGODB_PASSWORD", context.getAppName()));
    }

    public Map<String, Object> getSecretEntries() {
        return Map.of(
                "SPRING_DATA_MONGODB_USERNAME", "{{ (first .Values.mongodb.auth.usernames) | b64enc | quote }}",
                "SPRING_DATA_MONGODB_PASSWORD", "{{ (first .Values.mongodb.auth.passwords) | b64enc | quote }}");
    }

    public Map<String, String> getDefaultConfig() {
        return Map.of(
                "spring.data.mongodb.uri",
                "mongodb://{{ .Values.global.hosts.mongodb }}:{{ .Values.global.ports.mongodb }}/{{ .Values.mongodb.database }}");
    }

}
