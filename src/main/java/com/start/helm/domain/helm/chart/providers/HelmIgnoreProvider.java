package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmContext;
import org.springframework.stereotype.Component;

@Component
public class HelmIgnoreProvider implements HelmFileProvider {

	private static final String template = """
			.DS_Store
			# Common VCS dirs
			.git/
			.gitignore
			.bzr/
			.bzrignore
			.hg/
			.hgignore
			.svn/
			# Common backup files
			*.swp
			*.bak
			*.tmp
			*.orig
			*~
			# Various IDEs
			.project
			.idea/
			*.tmproj
			.vscode/

			""";

	@Override
	public String getFileContent(HelmContext context) {
		return template;
	}

	@Override
	public String getFileName() {
		return ".helmignore";
	}

}
