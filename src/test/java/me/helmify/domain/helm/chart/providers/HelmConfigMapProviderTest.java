package me.helmify.domain.helm.chart.providers;

import me.helmify.domain.helm.resolvers.FrameworkVendor;
import me.helmify.domain.helm.HelmContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HelmConfigMapProviderTest {

	@Test
	public void testConfigMap() {

		HelmConfigMapProvider provider = new HelmConfigMapProvider();
		HelmContext ctx = new HelmContext();

		ctx.setFrameworkVendor(FrameworkVendor.Quarkus);
		ctx.setAppName("app");
		ctx.setAppVersion("1");
		ctx.setHasActuator(true);
		ctx.setCreateIngress(true);

		String fileContent = provider.getFileContent(ctx);

		Assertions.assertTrue(fileContent.contains("    quarkus.management.enabled=true"));
		Assertions.assertTrue(fileContent.contains("    quarkus.management.port={{ .Values.healthcheck.port }}"));
		Assertions.assertTrue(fileContent.contains("    quarkus.http.port={{ .Values.service.port }}"));
		Assertions.assertTrue(fileContent.contains("    quarkus.log.level=DEBUG"));
		Assertions.assertTrue(fileContent.contains("    quarkus.log.min-level=DEBUG"));
		Assertions.assertTrue(fileContent.contains("    quarkus.log.console.enable=true"));
		Assertions.assertTrue(fileContent.contains("    quarkus.log.console.format=%d{HH:mm:ss} %-5p [%c] %s%e%n"));
		Assertions.assertTrue(fileContent.contains("    quarkus.application.name={{ .Values.fullnameOverride }}"));

	}

}
