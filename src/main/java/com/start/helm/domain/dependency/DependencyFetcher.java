package com.start.helm.domain.dependency;

import com.start.helm.domain.helm.HelmDependency;
import java.util.Optional;

public interface DependencyFetcher {
  Optional<HelmDependency> findDependency(String name);
}
