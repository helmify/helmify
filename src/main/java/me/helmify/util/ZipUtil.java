package me.helmify.util;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {

	@SneakyThrows
	private static void resetStream(InputStream zipInputStream) {
		try {
			zipInputStream.reset();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Optional<String> tryReadBuildFile(InputStream zipInputStream) {
		return Optional
			.ofNullable(ZipUtil.getZipContent("build.gradle.kts", new ZipInputStream(zipInputStream)).orElseGet(() -> {
				resetStream(zipInputStream);
				return ZipUtil.getZipContent("build.gradle", new ZipInputStream(zipInputStream)).orElseGet(() -> {
					resetStream(zipInputStream);
					return ZipUtil.getZipContent("pom.xml", new ZipInputStream(zipInputStream)).orElse(null);
				});
			}));
	}

	@SneakyThrows
	public static Optional<String> getZipContent(String ofFile, ZipInputStream zipInputStream) {
		ZipEntry entry;

		String dir = "";

		while ((entry = zipInputStream.getNextEntry()) != null) {
			String filename = entry.getName();
			if (entry.isDirectory() && "".equals(dir)) {
				dir = entry.getName();
				continue;
			}

			if (filename.endsWith(ofFile)) {
				byte[] buffer = new byte[10240];
				int len;

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				while ((len = zipInputStream.read(buffer)) > 0) {
					byteArrayOutputStream.write(buffer, 0, len);
				}
				final String pomXml = byteArrayOutputStream.toString(StandardCharsets.UTF_8);
				return Optional.of(pomXml);
			}
		}
		return Optional.empty();
	}

}
