package me.helmify.domain.ui.session;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.helmify.domain.helm.HelmContext;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class SessionInfo {

	String id;

	String appName;

	String appVersion;

	String frameworkVendor;

	String zipLink;

	boolean createIngress;

	boolean customized;

	Set<String> dependencyNames;

	@JsonIgnore
	HelmContext context;

	String dockerImageTag;

	String dockerImageRepositoryUrl;

	String dockerImagePullSecret;

	String chartFlavor;

	public static SessionInfo from(HelmContext c) {
		SessionInfo info = new SessionInfo();
		info.setId(String.valueOf(System.nanoTime()));
		info.setAppName(c.getAppName());
		info.setAppVersion(c.getAppVersion());
		info.setFrameworkVendor(c.getFrameworkVendor().name());
		info.setZipLink(c.getZipLink());
		info.setCreateIngress(c.isCreateIngress());
		info.setCustomized(c.getCustomized());
		info.setDependencyNames(c.getDependencyNames()
			.stream()
			.map(HelmContext.HelmDependencyName::getName)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet()));
		info.setContext(c);
		return info;
	}

}
