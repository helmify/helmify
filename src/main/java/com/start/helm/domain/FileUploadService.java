package com.start.helm.domain;

import com.start.helm.domain.helm.HelmContext;

public interface FileUploadService {

	HelmContext processBuildFile(String buildFile, String appName, String appVersion);

	HelmContext processBuildFile(String buildFile);

}
