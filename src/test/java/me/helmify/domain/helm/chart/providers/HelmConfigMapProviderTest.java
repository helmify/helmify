package me.helmify.domain.helm.chart.providers;

import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.HelmContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HelmConfigMapProviderTest {

	@Test
	public void testConfigMap() {

		HelmConfigMapProvider provider = new HelmConfigMapProvider();
		HelmContext ctx = new HelmContext();
		ctx.setChartFlavor("helm");

		ctx.setFrameworkVendor(FrameworkVendor.Quarkus);
		ctx.setAppName("app");
		ctx.setAppVersion("1");
		ctx.setHasActuator(true);
		ctx.setCreateIngress(true);

		String fileContent = provider.getFileContent(ctx);

		Assertions.assertTrue(fileContent.contains("QUARKUS_MANAGEMENT_ENABLED:"));
		Assertions.assertTrue(fileContent.contains("QUARKUS_MANAGEMENT_PORT:"));
		Assertions.assertTrue(fileContent.contains("QUARKUS_HTTP_PORT:"));
		Assertions.assertTrue(fileContent.contains("QUARKUS_LOG_LEVEL:"));
		Assertions.assertTrue(fileContent.contains("QUARKUS_LOG_MIN-LEVEL:"));
		Assertions.assertTrue(fileContent.contains("QUARKUS_LOG_CONSOLE_ENABLE:"));
		Assertions.assertTrue(fileContent.contains("QUARKUS_LOG_CONSOLE_FORMAT:"));
		Assertions.assertTrue(fileContent.contains("QUARKUS_APPLICATION_NAME:"));

	}

}
