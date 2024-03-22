package me.helmify.domain.build;

import me.helmify.domain.helm.HelmContext;

public interface FileUploadService {

	HelmContext processBuildFile(String buildFile, String appName, String appVersion);

	HelmContext processBuildFile(String buildFile);

	boolean shouldHandle(String filename);

}
