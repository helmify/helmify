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

    public Map<String, Object> getSecretEntries() {
        return Map.of(
                "SPRING_ELASTICSEARCH_USERNAME", "{{ .Values.elasticsearch.security.username | b64enc | quote }}"
                , "SPRING_ELASTICSEARCH_PASSWORD", "{{ .Values.elasticsearch.security.elasticPassword | b64enc | quote }}"
        );
    }


    public Map<String, String> getDefaultConfig() {
        return Map.of(
                "SPRING_ELASTICSEARCH_URIS",
                "http://{{ .Values.global.hosts.elasticsearch }}:{{ .Values.global.ports.elasticsearch }}"
        );
    }

}
