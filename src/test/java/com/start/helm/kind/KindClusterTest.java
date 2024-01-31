package com.start.helm.kind;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class KindClusterTest {

	@Test
	public void testCluster() {
		try (KubernetesClient client = new DefaultKubernetesClient()) {
			System.out.println("Services:");
			client.services()
				.list()
				.getItems()
				.stream()
				.map(service -> service.getMetadata().getName())
				.forEach(System.out::println);
			System.out.println("-----------------------------------");
			System.out.println("Pods:");
			client.pods()
				.list()
				.getItems()
				.stream()
				.map(pod -> pod.getMetadata().getName())
				.forEach(System.out::println);
			System.out.println("-----------------------------------");
			System.out.println("Deployments:");
			client.apps()
				.deployments()
				.list()
				.getItems()
				.stream()
				.map(deployment -> deployment.getMetadata().getName())
				.forEach(System.out::println);
		}
		catch (Exception e) {
			log.error("Error getting cluster info", e);
		}
	}

}
