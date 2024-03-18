package me.helmify.domain.helm.chart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class HelmFile {

	@Getter
	@Setter
	protected String fileName;

	@Getter
	@Setter
	protected String content;

}
