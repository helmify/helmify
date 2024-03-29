package me.helmify.domain.helm.dependencies.elasticsearch.quarkus;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.elasticsearch.ElasticSearchResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static me.helmify.util.HelmUtil.makeSecretKeyRef;

@Component
public class QsElasticSearchResolver implements ElasticSearchResolver {

	//@formatter:off
    @Override
    public List<String> matchOn() {
        return List.of("quarkus-elasticsearch");
    }


    public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
        return List.of(
                makeSecretKeyRef("QUARKUS_ELASTICSEARCH_USERNAME", "QUARKUS_ELASTICSEARCH_USERNAME", context.getAppName()),
                makeSecretKeyRef("QUARKUS_ELASTICSEARCH_PASSWORD", "QUARKUS_ELASTICSEARCH_PASSWORD", context.getAppName())
        );
    }


    public Map<String, Object> getSecretEntries() {
        return Map.of(
                "QUARKUS_ELASTICSEARCH_USERNAME", "{{ .Values.elasticsearch.security.username | b64enc | quote }}"
                , "QUARKUS_ELASTICSEARCH_PASSWORD", "{{ .Values.elasticsearch.security.elasticPassword | b64enc | quote }}"
        );
    }


    public Map<String, String> getDefaultConfig() {
        return Map.of(
                "%prod.quarkus.elasticsearch.hosts",
                "{{ .Values.global.hosts.elasticsearch }}:{{ .Values.global.ports.elasticsearch }}"
        );
    }

}
