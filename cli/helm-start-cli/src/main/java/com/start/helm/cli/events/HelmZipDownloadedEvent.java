package com.start.helm.cli.events;

public class HelmZipDownloadedEvent {

	private final byte[] zipFile;

	private final String path;

	public HelmZipDownloadedEvent(byte[] zipFile, String path) {
		this.zipFile = zipFile;
		this.path = path;
	}

	public byte[] getZipFile() {
		return zipFile;
	}

	public String getPath() {
		return path;
	}

}
