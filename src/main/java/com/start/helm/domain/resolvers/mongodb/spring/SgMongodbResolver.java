package com.start.helm.domain.resolvers.mongodb.spring;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.resolvers.mongodb.MongodbResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.start.helm.util.HelmUtil.makeSecretKeyRef;

@Component
public class SgMongodbResolver implements MongodbResolver {

	//@formatter:off

    @Override
    public List<String> matchOn() {
        return List.of("spring-boot-starter-data-mongodb", "spring-boot-starter-data-mongodb-reactive");
    }

    public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
        return List.of(
                makeSecretKeyRef("SPRING_DATA_MONGODB_USERNAME", "mongodb-username", context.getAppName()),
                makeSecretKeyRef("SPRING_DATA_MONGODB_PASSWORD", "mongodb-password", context.getAppName()));
    }

    public Map<String, Object> getSecretEntries() {
        return Map.of(
                "mongodb-username", "{{ (first .Values.mongodb.auth.usernames) | b64enc | quote }}",
                "mongodb-password", "{{ (first .Values.mongodb.auth.passwords) | b64enc | quote }}");
    }

    public Map<String, String> getDefaultConfig() {
        return Map.of(
                "spring.data.mongodb.uri",
                "mongodb://{{ .Values.global.hosts.mongodb }}:{{ .Values.global.ports.mongodb }}/{{ .Values.mongodb.database }}");
    }

}
