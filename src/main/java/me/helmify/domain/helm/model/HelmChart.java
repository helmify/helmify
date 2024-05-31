package me.helmify.domain.helm.model;

import lombok.*;

import java.util.List;

/**
 * Model for Helm Chart.yaml
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelmChart {

	private String apiVersion;

	private String name;

	private String version;

	private String appVersion;

	private String description;

	private HelmChartType type;

	private List<HelmChartDependency> dependencies;

	public enum HelmChartType {

		application, library

	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class HelmChartDependency {

		private String name;

		private String version;

		private String repository;

		private String condition;

		private List<String> tags;

	}

}
