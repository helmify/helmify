package me.helmify.domain.helm.bitnami;

import lombok.Getter;
import lombok.Setter;
import me.helmify.domain.helm.HelmContext;

@Getter
@Setter
public class BitnamiChartContext {

	private String chartName;

	// camelCased appName
	private String mainObjectBlock;

	private String componentName;

	private String imageName;

	private String imageTag;

	private String configFileName;

	private HelmContext originalContext;

	public static BitnamiChartContext from(HelmContext context) {
		String name = makeCamelCase(context.getAppName());
		BitnamiChartContext bitnamiChartContext = new BitnamiChartContext();
		bitnamiChartContext.setChartName(context.getAppName());
		bitnamiChartContext.setMainObjectBlock(name);
		bitnamiChartContext.setComponentName(context.getAppName());
		bitnamiChartContext.setImageName(context.getAppName());
		bitnamiChartContext.setImageTag(context.getAppVersion());
		// hardcode until we support something other than java
		bitnamiChartContext.setConfigFileName("application-prod.properties");
		bitnamiChartContext.setOriginalContext(context);
		return bitnamiChartContext;
	}

	private static String makeCamelCase(String string) {
		String[] parts = string.split("-");
		StringBuilder camelCaseString = new StringBuilder(parts[0]);
		for (int i = 1; i < parts.length; i++) {
			camelCaseString.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1));
		}
		return camelCaseString.toString();
	}

}
