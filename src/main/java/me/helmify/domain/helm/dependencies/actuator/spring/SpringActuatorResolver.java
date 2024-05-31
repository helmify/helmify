package me.helmify.domain.helm.dependencies.actuator.spring;

import me.helmify.domain.helm.HelmChartSlice;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.DependencyResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Resolver for spring actuator dependency.
 */
@Component
public class SpringActuatorResolver implements DependencyResolver {

	@Override
	public String dependencyName() {
		return "actuator";
	}

	@Override
	public List<String> matchOn() {
		return List.of("actuator");
	}

	@Override
	public Optional<HelmChartSlice> resolveDependency(HelmContext context) {
		context.setHasActuator(true);
		HelmChartSlice slice = new HelmChartSlice();
		slice.setValuesEntries(Map.of("healthcheck", Map.of("port", 8090, "name", "healthcheck")));
		return Optional.of(slice);
	}

}
