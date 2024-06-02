package me.helmify.domain.helm.dependencies.mongodb.quarkus;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.dependencies.mongodb.MongodbResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static me.helmify.util.HelmUtil.makeSecretKeyRef;

@Component
public class QsMongodbResolver implements MongodbResolver {

	@Override
	public List<String> matchOn() {
		return List.of("quarkus-mongodb-panache", "quarkus-mongodb-client");
	}

	public Map<String, Object> getSecretEntries() {
		return Map.of("QUARKUS_MONGODB_CREDENTIALS_USERNAME",
				"{{ (first .Values.mongodb.auth.usernames) | b64enc | quote }}", "QUARKUS_MONGODB_CREDENTIALS_PASSWORD",
				"{{ (first .Values.mongodb.auth.passwords) | b64enc | quote }}");
	}

	public Map<String, String> getDefaultConfig() {
		return Map.of("QUARKUS_MONGODB_CONNECTION-STRING",
				"mongodb://{{ .Values.global.hosts.mongodb }}:{{ .Values.global.ports.mongodb }}",
				"QUARKUS_MONGODB_DATABASE", "{{ .Values.mongodb.database }}");
	}

	@Override
	public FrameworkVendor getVendor() {
		return FrameworkVendor.Quarkus;
	}

}
