package com.start.helm.domain.helm;


import java.util.List;
import java.util.Objects;

/**
 * Model for an entry of the dependencies section in Chart.yaml
 * */
public record HelmDependency(String name, String version, String repository, List<String> tags){
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HelmDependency that = (HelmDependency) o;
    return Objects.equals(name, that.name) && Objects.equals(version, that.version) &&
        Objects.equals(repository, that.repository);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, version, repository);
  }
}
