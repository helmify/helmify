package me.helmify.domain.helm.chart.providers;

import lombok.SneakyThrows;
import me.helmify.domain.helm.HelmContext;
import org.springframework.core.io.ClassPathResource;

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

	default String patchContent(String content, HelmContext context) {
		return content;
	}

	/**
	 * Returns the name of the file under which the contents from getFileContent should be
	 * stored
	 */
	String getFileName();

	@SneakyThrows
	default String readTemplate(String path) {
		ClassPathResource classPathResource = new ClassPathResource(path);
		byte[] bytes = classPathResource.getInputStream().readAllBytes();
		return new String(bytes);
	}

}
