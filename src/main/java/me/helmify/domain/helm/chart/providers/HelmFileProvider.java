package me.helmify.domain.helm.chart.providers;

import me.helmify.domain.helm.HelmContext;

/**
 * HelmFileProviders are responsible for providing the plaintext file contents of an
 * individual Helm chart file. Any customizations, extensions, etc. which are specific to
 * a particular file are implemented here.
 */
public interface HelmFileProvider {

	/**
	 * Returns the contents to be written to the file identified by getFileName.
	 * <p/>
	 * We have access to the {@link HelmContext} here which allows us to perform any
	 * customizations of the helm chart file before returning its contents.
	 */
	String getFileContent(HelmContext context);

	/**
	 * Returns the name of the file under which the contents from getFileContent should be
	 * stored
	 */
	String getFileName();

}
