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

    public Map<String, Object> getSecretEntries() {
        return Map.of(
                "SPRING_COUCHBASE_USERNAME", "{{ .Values.couchbase.dbUser.user | b64enc | quote }}",
                "SPRING_COUCHBASE_PASSWORD", "{{ .Values.couchbase.dbUser.password | b64enc | quote }}"
        );
    }


    public Map<String, String> getDefaultConfig() {
        return Map.of(
                "SPRING_COUCHBASE_ENV_TIMEOUTS_VIEW", "15000",
                "SPRING_COUCHBASE_ENV_TIMEOUTS_QUERY", "15000",
                "SPRING_DATA_COUCHBASE_BUCKET-NAME", "{{ .Values.couchbase.bucketName }}",
                "SPRING_DATA_COUCHBASE_AUTO-INDEX", "true",
                "SPRING_COUCHBASE_CONNECTION-STRING","couchbase://{{ .Values.global.hosts.couchbase }}.{{ .Release.Namespace }}.svc.cluster.local"
        );
    }

}
