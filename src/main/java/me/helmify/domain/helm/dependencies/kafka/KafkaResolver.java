package me.helmify.domain.helm.dependencies.kafka;

import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.DependencyResolver;

import java.util.Map;

public interface KafkaResolver extends DependencyResolver {

	//@formatter:off


    default Map<String, Object> getValuesEntries(HelmContext context) {
        return Map.of(
                "kafka",
                Map.of("enabled", true,
                        "port", getKafkaPort() ,
                        "nameOverride", context.getAppName() + "-kafka",
                        "fullnameOverride", context.getAppName() + "-kafka",
                        "listeners", Map.of(
                                "client", Map.of(
                                        "protocol", "PLAINTEXT", // Allowed values are 'PLAINTEXT', 'SASL_PLAINTEXT', 'SASL_SSL' and 'SSL'
                                        "sslClientAuth", "none" // Allowed values are 'none', 'required' and 'requested'
                                )
                        )
                ),
                "global", Map.of("hosts", Map.of("kafka", context.getAppName() + "-kafka"), "ports", Map.of("kafka", getKafkaPort() ))
        );
    }

    default Map<String, Object> getPreferredChart() {
        return Map.of(
                "name", "kafka",
                "version", "26.4.2",
                "repository", "https://charts.bitnami.com/bitnami"
        );
    }
    default int getKafkaPort() {
        return 9092;
    }

    @Override
    default String dependencyName() {
        return "kafka";
    }


}
