package com.start.helm.domain.helm.chart.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model for Helm Chart.yaml
 * */
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
