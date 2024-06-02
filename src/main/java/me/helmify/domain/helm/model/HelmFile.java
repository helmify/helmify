package me.helmify.domain.helm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class HelmFile {

	@Setter
	protected String fileName;

	@Getter
	@Setter
	protected String content;

	public String getFileName() {
		return "templates/" + fileName;
	}

}
