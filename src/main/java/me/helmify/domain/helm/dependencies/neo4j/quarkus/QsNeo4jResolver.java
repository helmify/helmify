package me.helmify.domain.helm.dependencies.neo4j.quarkus;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.neo4j.Neo4jResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static me.helmify.util.HelmUtil.makeSecretKeyRef;

@Component
public class QsNeo4jResolver implements Neo4jResolver {

	@Override
	public List<String> matchOn() {
		return List.of("quarkus-neo4j");
	}

	@Override
	public Map<String, Object> getSecretEntries() {
		return Map.of("QUARKUS_NEO4J_AUTHENTICATION_USERNAME", "{{ .Values.neo4j.neo4j.user | b64enc | quote }}",
				"QUARKUS_NEO4J_AUTHENTICATION_PASSWORD", "{{ .Values.neo4j.neo4j.password | b64enc | quote }}");

	}

	public Map<String, String> getDefaultConfig() {
		return Map.of("QUARKUS_NEO4J_URI", "bolt://{{ .Values.global.hosts.neo4j }}:{{ .Values.global.ports.neo4j }}");
	}

}
