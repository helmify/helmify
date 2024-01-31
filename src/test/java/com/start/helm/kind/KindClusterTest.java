package com.start.helm.kind;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class KindClusterTest {

	private List<String> getProperty(String propertyName) {
		String property = System.getProperty(propertyName);
		if (property == null || property.isEmpty()) {
			return List.of();
		}
		return property.contains(",") ? List.of(property.split(",")) : List.of(property);
	}

	@Test
	public void testCluster() {

		// only execute when explicitly called
		String property = System.getProperty("test");
		if (property == null || property.isEmpty()) {
			return;
		}

		List<String> expectedDeployments = getProperty("expected-deployments");
		List<String> expectedServices = getProperty("expected-services");
		List<String> expectedPods = getProperty("expected-pods");

		try (KubernetesClient client = new DefaultKubernetesClient()) {
			System.out.println("Services:");
			List<String> actualServices = client.services()
				.list()
				.getItems()
				.stream()
				.map(service -> service.getMetadata().getName())
				.toList();

			boolean allExpectedServicesPresent = actualServices.containsAll(expectedServices);
			Assertions.assertTrue(allExpectedServicesPresent, "Not all expected services found: " + expectedServices);

			System.out.println("-----------------------------------");
			System.out.println("Pods:");
			List<String> actualPods = client.pods()
				.list()
				.getItems()
				.stream()
				.map(pod -> pod.getMetadata().getName())
				.toList();

			boolean allExpectedPodsPresent = actualPods.containsAll(expectedPods);
			Assertions.assertTrue(allExpectedPodsPresent, "Not all expected pods found: " + expectedPods);

			System.out.println("-----------------------------------");
			System.out.println("Deployments:");
			List<String> actualDeployments = client.apps()
				.deployments()
				.list()
				.getItems()
				.stream()
				.map(deployment -> deployment.getMetadata().getName())
				.toList();

			boolean allExpectedDeploymentsPresent = actualDeployments.containsAll(expectedDeployments);
			Assertions.assertTrue(allExpectedDeploymentsPresent,
					"Not all expected deployments found: " + expectedDeployments);

			System.out.println("-----------------------------------");
			System.out.println("Secrets:");
			client.secrets()
				.list()
				.getItems()
				.stream()
				.map(secret -> secret.getMetadata().getName())
				.forEach(System.out::println);
			System.out.println("-----------------------------------");
			System.out.println("ConfigMaps:");
			client.configMaps()
				.list()
				.getItems()
				.stream()
				.map(configMap -> configMap.getMetadata().getName())
				.forEach(System.out::println);
			System.out.println("-----------------------------------");
			System.out.println("Volumes:");
			client.persistentVolumes()
				.list()
				.getItems()
				.stream()
				.map(volume -> volume.getMetadata().getName())
				.forEach(System.out::println);

		}
		catch (Exception e) {
			log.error("Error getting cluster info", e);
		}
	}

}
