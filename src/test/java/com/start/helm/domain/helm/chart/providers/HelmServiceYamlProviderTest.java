package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

class HelmServiceYamlProviderTest {

	@Test
	void getFileContent() {

		HelmServiceYamlProvider helmServiceYamlProvider = new HelmServiceYamlProvider();

		HelmContext ctx = new HelmContext();
		ctx.setHasActuator(true);

		String fileContent = helmServiceYamlProvider.getFileContent(ctx);
		Set<Integer> collect = fileContent.lines()
			.filter(p -> p.contains("- port:"))
			.map(this::getLeadingWhitespace)
			.collect(Collectors.toSet());

		Assertions.assertEquals(1, collect.size());
		Assertions.assertEquals(4, collect.iterator().next(),
				"port entry in service.yaml should have 4 leading whitespaces");

	}

	private int getLeadingWhitespace(String str) {
		int i = 0;
		while (i < str.length() && Character.isWhitespace(str.charAt(i))) {
			i++;
		}
		return i;
	}

}
