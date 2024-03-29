package me.helmify.domain.helm.dependencies.elasticsearch.spring;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.elasticsearch.ElasticSearchResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static me.helmify.util.HelmUtil.makeSecretKeyRef;

@Component
public class SgElasticSearchResolver implements ElasticSearchResolver {

	//@formatter:off
    @Override
    public List<String> matchOn() {
        return List.of("spring-data-elasticsearch", "spring-boot-starter-data-elasticsearch");
    }


    public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
        return List.of(
                makeSecretKeyRef("SPRING_ELASTICSEARCH_USERNAME", "SPRING_ELASTICSEARCH_USERNAME", context.getAppName()),
                makeSecretKeyRef("SPRING_ELASTICSEARCH_PASSWORD", "SPRING_ELASTICSEARCH_PASSWORD", context.getAppName())
        );
    }


    public Map<String, Object> getSecretEntries() {
        return Map.of(
                "SPRING_ELASTICSEARCH_USERNAME", "{{ .Values.elasticsearch.security.username | b64enc | quote }}"
                , "SPRING_ELASTICSEARCH_PASSWORD", "{{ .Values.elasticsearch.security.elasticPassword | b64enc | quote }}"
        );
    }


    public Map<String, String> getDefaultConfig() {
        return Map.of(
                "spring.elasticsearch.uris",
                "http://{{ .Values.global.hosts.elasticsearch }}:{{ .Values.global.ports.elasticsearch }}"
        );
    }

}
