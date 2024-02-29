package com.start.helm.domain.resolvers.elasticsearch.spring;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.resolvers.elasticsearch.ElasticSearchResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.start.helm.util.HelmUtil.makeSecretKeyRef;

@Component
public class SgElasticSearchResolver implements ElasticSearchResolver {

	//@formatter:off
    @Override
    public List<String> matchOn() {
        return List.of("spring-data-elasticsearch", "spring-boot-starter-data-elasticsearch");
    }


    public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
        return List.of(
                makeSecretKeyRef("SPRING_ELASTICSEARCH_USERNAME", "elasticsearch-username", context.getAppName()),
                makeSecretKeyRef("SPRING_ELASTICSEARCH_PASSWORD", "elasticsearch-password", context.getAppName())
        );
    }


    public Map<String, Object> getSecretEntries() {
        return Map.of(
                "elasticsearch-username", "{{ .Values.elasticsearch.security.username | b64enc | quote }}"
                , "elasticsearch-password", "{{ .Values.elasticsearch.security.elasticPassword | b64enc | quote }}"
        );
    }


    public Map<String, String> getDefaultConfig() {
        return Map.of(
                "spring.elasticsearch.uris",
                "http://{{ .Values.global.hosts.elasticsearch }}:{{ .Values.global.ports.elasticsearch }}"
        );
    }

}
