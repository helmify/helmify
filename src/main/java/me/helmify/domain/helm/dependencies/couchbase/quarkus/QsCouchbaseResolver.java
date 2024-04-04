package me.helmify.domain.helm.dependencies.couchbase.quarkus;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.couchbase.CouchbaseResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static me.helmify.util.HelmUtil.makeSecretKeyRef;

@Component
public class QsCouchbaseResolver implements CouchbaseResolver {

	//@formatter:off
    @Override
    public List<String> matchOn() {
        return List.of("quarkus-couchbase");
    }

    public Map<String, Object> getSecretEntries() {
        return Map.of(
                "QUARKUS_COUCHBASE_USERNAME", "{{ .Values.couchbase.dbUser.user | b64enc | quote }}",
                "QUARKUS_COUCHBASE_PASSWORD", "{{ .Values.couchbase.dbUser.password | b64enc | quote }}"
        );
    }


    public Map<String, String> getDefaultConfig() {
        return Map.of(
                "QUARKUS_COUCHBASE_CONNECTION-STRING","{{ .Values.global.hosts.couchbase }}.{{ .Release.Namespace }}.svc.cluster.local:{{ .Values.global.ports.couchbase }}"
        );
    }

    @Override
    public FrameworkVendor getVendor() {
        return FrameworkVendor.Quarkus;
    }

}
