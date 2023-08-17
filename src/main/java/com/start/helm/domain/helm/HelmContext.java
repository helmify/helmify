package com.start.helm.domain.helm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Processing Context for new Helm Chart.
 * <p>
 * Here we prepare "things to do".
 */
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
  private final Set<HelmChartFragment> helmChartFragments = new HashSet<>();

  @Getter
  private final List<Map<String, Object>> valuesGlobalBlocks = new ArrayList<>();

  public void addValuesGlobalBlock(Map<String, Object> valuesGlobalBlock) {
    this.valuesGlobalBlocks.add(valuesGlobalBlock);
  }

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
