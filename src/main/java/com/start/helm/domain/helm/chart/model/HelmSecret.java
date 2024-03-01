package com.start.helm.domain.helm.chart.model;

import com.start.helm.domain.helm.chart.customizers.TemplateStringPatcher;
import com.start.helm.util.HelmUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class HelmSecret {

	@Getter
	@Setter
	private String fileName;

	@Getter
	@Setter
	private String secretName;

	@Getter
	@Setter
	private String stringData;

	private static final String yaml = """
			apiVersion: v1
			kind: Secret
			metadata:
			  name: SECRET_NAME
			type: Opaque
			data:
			###@helm-start:data
			""";

	public String getYaml() {
		String patched = TemplateStringPatcher.insertAfter(yaml, "###@helm-start:data", stringData, 2)
			.replace("SECRET_NAME", secretName);
		return HelmUtil.removeMarkers(patched);
	}

}
