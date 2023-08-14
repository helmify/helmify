package com.start.helm.domain.helm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class HelmContext {

  @Getter
  @Setter
  private boolean createIngress = false;

  @Getter
  @Setter
  private String appName;
  @Getter
  @Setter
  private String appVersion;
  @Getter
  @Setter
  private FrameworkVendor frameworkVendor = FrameworkVendor.Spring;

  @Getter
  private final Set<HelmDependency> helmDependencies = new HashSet<>();

  @Getter
  private Set<HelmChartFragment> helmChartFragments = new HashSet<>();

  public void addHelmChartFragment(HelmChartFragment helmChartFragment) {
    this.helmChartFragments.add(helmChartFragment);
  }

  public void addHelmDependency(HelmDependency helmDependency) {
    this.helmDependencies.add(helmDependency);
  }

  public enum FrameworkVendor {
    Spring
    //Quarkus
    //Micronaut
    //...
  }

}
