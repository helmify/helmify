package me.helmify.domain;

import me.helmify.domain.helm.HelmContext;

public interface FileUploadService {

	HelmContext processBuildFile(String buildFile, String appName, String appVersion);

	HelmContext processBuildFile(String buildFile);

}
