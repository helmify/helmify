package me.helmify.domain.maven;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.FileUploadService;
import me.helmify.domain.helm.HelmContext;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MavenFileUploadService implements FileUploadService {

	private final MavenModelProcessor mavenModelProcessor;

	public HelmContext processBuildFile(String pomXml) {
		org.apache.maven.api.model.Model m = MavenModelParser.parsePom(pomXml).orElseThrow();
		HelmContext helmContext = mavenModelProcessor.process(m);
		helmContext.setAppVersion(m.getVersion());
		helmContext.setAppName(m.getArtifactId());
		helmContext.setDependencyDescriptor(pomXml);
		return helmContext;

	}

	@Override
	public HelmContext processBuildFile(String buildFile, String appName, String appVersion) {
		HelmContext helmContext = processBuildFile(buildFile);
		helmContext.setAppVersion(appVersion);
		helmContext.setAppName(appName);
		return helmContext;
	}

}
