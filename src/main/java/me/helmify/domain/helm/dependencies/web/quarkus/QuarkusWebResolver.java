package me.helmify.domain.helm.dependencies.web.quarkus;

import me.helmify.domain.helm.HelmChartSlice;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.DependencyResolver;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Resolver for quarkus web dependencies.
 */
@Component
public class QuarkusWebResolver implements DependencyResolver {

	@Override
	public String dependencyName() {
		return "web";
	}

	@Override
	public List<String> matchOn() {
		return List.of("quarkus-vertx-graphql", "quarkus-smallrye-graphql", "quarkus-resteasy-reactive-jackson");
	}

	@Override
	public Optional<HelmChartSlice> resolveDependency(HelmContext context) {
		context.setCreateIngress(true);
		return Optional.empty();
	}

	@Override
	public FrameworkVendor getVendor() {
		return FrameworkVendor.Quarkus;
	}

}
