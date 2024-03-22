package me.helmify.domain.ui;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.helm.chart.providers.HelmFileProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZipFileService {

	private final List<HelmFileProvider> providers;

	@SneakyThrows
	public void streamZip(HelmContext context, HttpServletResponse response, String filename) {

		OutputStream outputStream = response.getOutputStream();
		ZipOutputStream zos = new ZipOutputStream(outputStream);
		ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
		addHelmFiles(context, zos, in);

		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
		response.setContentType("application/octet-stream");

		zos.flush();
		zos.close();
		outputStream.flush();
	}

	@SneakyThrows
	public void streamZip(HelmContext context, byte[] originalStarter, HttpServletResponse response, String filename) {
		final ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());

		Optional.of(originalStarter)
			.map(ByteArrayInputStream::new)
			.map(bytes -> writeExistingZipEntries(zos, bytes))
			.map(bytes -> addHelmFiles(context, zos, bytes))
			.ifPresent(bytes -> {
				try {
					response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
					response.setContentType("application/octet-stream");

					zos.flush();
					zos.close();
					response.getOutputStream().flush();
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			});

	}

	private void addTemplatesDir(String helmDir, ZipOutputStream zos) throws Exception {
		String templatesDir = helmDir + "templates/";

		ZipEntry templatesDirEntry = new ZipEntry(templatesDir);
		zos.putNextEntry(templatesDirEntry);
		zos.closeEntry();

	}

	private String addHelmDir(ByteArrayInputStream bytes, ZipOutputStream zos) throws Exception {
		ZipInputStream zip = new ZipInputStream(bytes);

		ZipEntry nextEntry = zip.getNextEntry();

		if (nextEntry == null) {
			// empty zip
			String helmDirName = "helm/";
			addHelmDirZipEntry(zos, helmDirName);
			return helmDirName;
		}
		else {
			// existing zip
			ZipEntry next;
			do {
				next = zip.getNextEntry();
				if (next != null && next.getName().endsWith("src/")) {
					String helmDirName = next.getName().replace("src/", "helm/");
					addHelmDirZipEntry(zos, helmDirName);
					return helmDirName;
				}
			}
			while (next != null);
		}
		return null;
	}

	private static void addHelmDirZipEntry(ZipOutputStream zos, String helmDirName) throws IOException {
		ZipEntry helmDir = new ZipEntry(helmDirName);
		zos.putNextEntry(helmDir);
		zos.closeEntry();
	}

	@SneakyThrows
	private ZipOutputStream addHelmFiles(HelmContext context, ZipOutputStream zos, ByteArrayInputStream bytes) {
		bytes.reset();

		String helmDir = addHelmDir(bytes, zos);
		addTemplatesDir(helmDir, zos);

		providers.forEach(p -> {
			String fileContent = p.getFileContent(context);
			String fileName = p.getFileName();
			addZipEntry(helmDir + fileName, fileContent, zos);
		});

		context.getAllExtraFiles().forEach(extraFile -> {
			String fileContent = extraFile.getContent();
			String fileName = helmDir + extraFile.getFileName();
			log.info("Adding extra file {}", fileName);
			addZipEntry(fileName, fileContent, zos);
		});

		return zos;
	}

	private ByteArrayInputStream writeExistingZipEntries(ZipOutputStream zos, ByteArrayInputStream bytes) {
		ZipInputStream zip = new ZipInputStream(bytes);

		try {
			ZipEntry e;
			do {
				e = zip.getNextEntry();
				if (e != null) {
					zos.putNextEntry(e);
					byte[] buffer = new byte[1024];
					int len;
					while ((len = zip.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
					zos.closeEntry();
				}
			}
			while (e != null);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		return bytes;
	}

	private void addZipEntry(String filename, String content, ZipOutputStream zipOutputStream) {
		ZipEntry zipEntry = new ZipEntry(filename);
		try {
			zipOutputStream.putNextEntry(zipEntry);
			zipOutputStream.write(content.getBytes());
			zipOutputStream.closeEntry();
		}
		catch (Exception e) {
			log.error("Error while processing file: {}", filename, e);
		}
	}

}
