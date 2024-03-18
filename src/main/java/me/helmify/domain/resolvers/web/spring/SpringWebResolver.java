package me.helmify.domain.resolvers.web.spring;

import me.helmify.domain.helm.HelmChartSlice;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.resolvers.DependencyResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Resolver for spring web dependencies.
 */
@Component
public class SpringWebResolver implements DependencyResolver {

	@Override
	public String dependencyName() {
		return "web";
	}

	@Override
	public List<String> matchOn() {
		return List.of("spring-boot-starter-web", "spring-boot-starter-webflux", "spring-boot-starter-graphql");
	}

	@Override
	public Optional<HelmChartSlice> resolveDependency(HelmContext context) {
		context.setCreateIngress(true);
		return Optional.empty();
	}

}
