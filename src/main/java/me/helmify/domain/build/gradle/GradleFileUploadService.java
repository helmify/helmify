package me.helmify.domain.build.gradle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.build.FileUploadService;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.dependencies.DependencyResolver;
import me.helmify.util.GradleUtil;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradleFileUploadService implements FileUploadService {

	private final List<DependencyResolver> resolvers;

	public HelmContext processBuildFile(String buildFile) {
		return processBuildFile(buildFile, "my-project", GradleUtil.extractVersion(buildFile));
	}

	/**
	 * Method which accepts a build.gradle file as String and builds a {@link HelmContext}
	 * from it.
	 * <p/>
	 * A note on gradle handling: After looking into gradle's tooling api, this simpler
	 * approach has proven as effective in extracting the few bits of data we need to
	 * populate a {@link HelmContext}.
	 */
	public HelmContext processBuildFile(String buildFile, String appName, String appVersion) {
		log.info("Processing Gradle Build: {}", buildFile);

		HelmContext context = new HelmContext();

		context.setAppName(appName);
		context.setAppVersion(appVersion);
		context.setDependencyDescriptor(buildFile);

		List<String> dependencies = buildFile.lines()
			.filter(line -> line.contains("implementation") || line.contains("runtimeOnly"))
			.map(String::trim)
			.map(line -> line.replace("runtimeOnly", ""))
			.map(line -> line.replace("implementation", ""))
			.map(line -> line.replace("'", ""))
			.map(line -> line.replace("\"", ""))
			.map(line -> line.replace("(", "").replace(")", ""))
			.toList();
		List<String> groupIds = dependencies.stream().map(line -> line.split(":")[0]).toList();
		FrameworkVendor frameworkVendor = HelmUtil.getFrameworkVendor(groupIds);
		context.setFrameworkVendor(frameworkVendor);

		dependencies.stream()
			.map(line -> line.split(":")[1])
			.flatMap(artifactId -> resolvers.stream()
				.filter(matcher -> frameworkVendor.equals(matcher.getVendor()))
				.filter(matcher -> matcher.matches(artifactId))
				.map(matcher -> matcher.resolveDependency(context))
				.filter(Optional::isPresent)
				.map(Optional::get))
			.collect(Collectors.toSet())
			.forEach(context::addHelmChartFragment);

		return context;
	}

	public boolean shouldHandle(String filename) {
		return filename.contains(".gradle");
	}

}
