package com.start.helm.domain.helm.chart.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HelmChart {

  private String apiVersion;
  private String name;
  private String version;
  private String appVersion;
  private String description;
  private HelmChartType type;

  public enum HelmChartType {
    application, library
  }

}
