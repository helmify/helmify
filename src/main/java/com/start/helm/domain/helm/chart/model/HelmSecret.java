package com.start.helm.domain.helm.chart.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.start.helm.domain.helm.chart.customizers.TemplateStringPatcher;
import com.start.helm.util.HelmUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class HelmSecret extends HelmFile {

	public HelmSecret(String filename, String secretName, String stringData) {
		this.fileName = filename;
		this.secretName = secretName;
		this.stringData = stringData;
	}

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

	@JsonIgnore
	public String getYaml() {
		String patched = TemplateStringPatcher.insertAfter(yaml, "###@helm-start:data", stringData, 2)
			.replace("SECRET_NAME", secretName);
		return HelmUtil.removeMarkers(patched);
	}

}
