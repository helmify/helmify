package me.helmify.domain.helm.dependencies.couchbase.spring;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.couchbase.CouchbaseResolver;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SgCouchbaseResolver implements CouchbaseResolver {

	//@formatter:off
    @Override
    public List<String> matchOn() {
        return List.of("spring-boot-starter-data-couchbase", "spring-data-couchbase");
    }


    public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
        return List.of(
                HelmUtil.makeSecretKeyRef("SPRING_COUCHBASE_USERNAME", "SPRING_COUCHBASE_USERNAME", context.getAppName()),
                HelmUtil.makeSecretKeyRef("SPRING_COUCHBASE_PASSWORD", "SPRING_COUCHBASE_PASSWORD", context.getAppName())
        );
    }


    public Map<String, Object> getSecretEntries() {
        return Map.of(
                "SPRING_COUCHBASE_USERNAME", "{{ .Values.couchbase.dbUser.user | b64enc | quote }}",
                "SPRING_COUCHBASE_PASSWORD", "{{ .Values.couchbase.dbUser.password | b64enc | quote }}"
        );
    }


    public Map<String, String> getDefaultConfig() {
        return Map.of(
                "spring.couchbase.env.timeouts.view", "15000",
                "spring.couchbase.env.timeouts.query", "15000",
                "spring.data.couchbase.bucket-name", "{{ .Values.couchbase.bucketName }}",
                "spring.data.couchbase.auto-index", "true",
                "spring.couchbase.connection-string","couchbase://{{ .Values.global.hosts.couchbase }}.{{ .Release.Namespace }}.svc.cluster.local"
        );
    }

}
