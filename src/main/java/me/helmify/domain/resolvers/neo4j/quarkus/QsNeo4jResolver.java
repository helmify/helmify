package me.helmify.domain.resolvers.neo4j.quarkus;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.resolvers.neo4j.Neo4jResolver;
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
	public List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
		return List.of(
				makeSecretKeyRef("QUARKUS_NEO4J_AUTHENTICATION_USERNAME", "neo4j-username", context.getAppName()),
				makeSecretKeyRef("QUARKUS_NEO4J_AUTHENTICATION_PASSWORD", "neo4j-password", context.getAppName()));
	}

	@Override
	public Map<String, Object> getSecretEntries() {
		return Map.of("neo4j-username", "{{ .Values.neo4j.neo4j.user | b64enc | quote }}", "neo4j-password",
				"{{ .Values.neo4j.neo4j.password | b64enc | quote }}");

	}

	public Map<String, String> getDefaultConfig() {
		return Map.of("quarkus.neo4j.uri", "bolt://{{ .Values.global.hosts.neo4j }}:{{ .Values.global.ports.neo4j }}");
	}

}
