package com.start.helm.domain.resolvers.web.quarkus;

import com.start.helm.domain.helm.HelmChartSlice;
import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.resolvers.DependencyResolver;
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

}
