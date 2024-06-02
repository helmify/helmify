package me.helmify.domain.helm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.helmify.domain.helm.chart.TemplateStringPatcher;
import me.helmify.util.HelmUtil;

@NoArgsConstructor
public class HelmSecret extends HelmFile {

	public HelmSecret(String filename, String secretName, String content) {
		this.fileName = filename;
		this.secretName = secretName;
		this.content = content;
	}

	@Getter
	@Setter
	private String secretName;

	private static final String yaml = """
			apiVersion: v1
			kind: Secret
			metadata:
			  name: SECRET_NAME
			type: Opaque
			data:
			###@helmify:data
			""";

	@JsonIgnore
	public String getYaml() {
		String patched = TemplateStringPatcher.insertAfter(yaml, "###@helmify:data", content, 2)
			.replace("SECRET_NAME", secretName);
		return HelmUtil.removeMarkers(patched);
	}

	@Override
	public String getContent() {
		return getYaml();
	}

}
