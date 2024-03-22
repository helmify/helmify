package me.helmify.domain.helm.resolvers;

import me.helmify.domain.helm.HelmChartSlice;
import me.helmify.domain.helm.HelmChartSliceBuilder;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.chart.model.HelmFile;
import me.helmify.domain.helm.chart.model.HelmSecret;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.helmify.util.HelmUtil.initContainer;

/**
 * Interface defining a DependencyResolver.
 * <p/>
 * A DependencyResolver is responsible for resolving a Maven dependency to a Helm
 * Dependency.
 */
public interface DependencyResolver extends HelmChartSliceBuilder {

	/**
	 * List of Strings against which Maven ArtifactIds are checked with a contains-check.
	 */
	List<String> matchOn();

	/**
	 * Method for contains-matching a Maven ArtifactId against the matchOn list.
	 */
	default boolean matches(String artifactId) {
		return matchOn().stream().anyMatch(artifactId::contains);
	}

	/**
	 * Method for providing a {@link HelmChartSlice} through this DependencyResolver.
	 * <p/>
	 * The {@link Optional} may be empty if the Maven Dependency does not have an
	 * equivalent on the infrastructure side (such as "org.postgresql:postgresql" relating
	 * to a database). The {@link HelmContext} is still accessible here though to ie set
	 * properties.
	 * <p/>
	 * If the Maven Dependency has an equivalent on the infrastructure side, a
	 * {@link HelmChartSlice} must be returned.
	 */
	default Optional<HelmChartSlice> resolveDependency(HelmContext context) {

		HelmChartSlice slice = new HelmChartSlice();
		slice.setEnvironmentEntries(getEnvironmentEntries(context));
		slice.setDefaultConfig(getDefaultConfig());
		slice.setPreferredChart(getPreferredChart());
		slice.setValuesEntries(getValuesEntries(context));
		slice.setSecretEntries(getSecretEntries());

		String endpoint = initContainerCheckEndpoint(context).replace("%s", dependencyName());
		Map<String, Object> initContainer = initContainer(context.getAppName(), dependencyName(), endpoint);
		slice.setInitContainer(initContainer);
		slice.setExtraSecrets(getExtraSecrets(context));
		slice.setExtraFiles(getExtraFiles(context));
		slice.setDependencyName(dependencyName());

		return Optional.of(slice);
	}

	/**
	 * Name of the dependency.
	 */
	String dependencyName();

	default FrameworkVendor getVendor() {
		return FrameworkVendor.Spring;
	}

	default List<HelmSecret> getExtraSecrets(HelmContext context) {
		return new ArrayList<>();
	}

	default List<HelmFile> getExtraFiles(HelmContext context) {
		return new ArrayList<>();
	}

}
