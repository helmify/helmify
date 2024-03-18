package me.helmify.domain.resolvers.couchbase;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.chart.model.HelmFile;
import me.helmify.domain.resolvers.DependencyResolver;

import java.util.List;
import java.util.Map;

public interface CouchbaseResolver extends DependencyResolver {

	//@formatter:off

    default String getBucketname(HelmContext context) {
        return context.getAppName().replace("-", "");
    }

    default Map<String, Object> getValuesEntries(HelmContext context) {
        return Map.of(
				"couchbase", Map.of(
						"enabled", true,
                        "bucketName", getBucketname(context),
                        "port", getPort(),
                        "dbUser", Map.of(
                                "user", "couchbase",
                                "password", "couchbase"
                        )
                ),
                "global", Map.of(
                        "hosts", Map.of(
                                "couchbase", getClustername(context)),
                        "ports", Map.of(
                                "couchbase", getPort())));
    }

    default Map<String, String> getPreferredChart() {
        return Map.of(  );
    }

	String secretName = "couchbase-secret";

    default String getClustername(HelmContext context)  {
        return context.getAppName().replace("-", "") + "-cluster";
    }

	@Override
	default List<HelmFile> getExtraFiles(HelmContext context) {
		String clusterName = getClustername(context);

		return List.of(
				new HelmFile("couchbase-cluster.yaml", """
apiVersion: v1
kind: Secret
metadata:
  name: @@secretname
type: Opaque
data:
  username: {{ .Values.couchbase.dbUser.user | b64enc | quote }}
  password: {{ .Values.couchbase.dbUser.password | b64enc | quote }}
---
apiVersion: couchbase.com/v2
kind: CouchbaseBucket
metadata:
  name: @@bucketname
spec:
  memoryQuota: 128Mi
---
apiVersion: couchbase.com/v2
kind: CouchbaseCluster
metadata:
  name: @@clustername
spec:
  image: couchbase/server:7.1.3
  security:
    adminSecret: @@secretname
  networking:
    exposeAdminConsole: true
    adminConsoleServices:
    - data
  buckets:
    managed: true
  servers:
  - size: 3
    name: all_services
    services:
    - data
    - index
    - query
    - search
    - eventing
""".replace("@@bucketname", getBucketname(context))
    .replace("@@clustername", clusterName)
    .replace("@@secretname", secretName)
                )

	);

    }

	default int getPort() {
        return 8093;
    }

    @Override
    default String dependencyName() {
        return "couchbase";
    }


}
