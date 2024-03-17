package me.helmify.kind;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class KindWaitTest {

	@Test
	public void testCluster() throws Exception {

		// only execute when explicitly called
		String property = System.getProperty("test");
		if (property == null || property.isEmpty() || !"KindWait".equals(property)) {
			return;
		}

		String timeoutSeconds = System.getProperty("timeout-seconds", "600");
		long timeout = Long.parseLong(timeoutSeconds);

		// Wait for the cluster to be ready
		waitForAllPods(timeout);

	}

	public static void waitForAllPods(long timeout) {
		DefaultKubernetesClient client = new DefaultKubernetesClient();

		log.info("Waiting for All Pods to be Ready");

		long timePassed = 0;
		boolean allPodsReady = false;
		do {
			List<Pod> pods = client.pods().list().getItems();
			allPodsReady = pods.stream().allMatch(pod -> "Running".equals(pod.getStatus().getPhase()));
			if (!allPodsReady) {
				try {
					long sleep = 2000;
					timePassed += (sleep / 1000);
					Thread.sleep(sleep);
				}
				catch (InterruptedException e) {
					log.error("Error waiting for pods to be ready", e);
				}
			}

			if (timePassed >= timeout) {
				throw new RuntimeException("Timeout waiting for pods to be ready");
			}
		}
		while (!allPodsReady);

	}

}
