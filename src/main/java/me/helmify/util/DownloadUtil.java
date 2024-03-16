package me.helmify.util;

import org.springframework.http.HttpHeaders;

public class DownloadUtil {

	public static HttpHeaders headers(String filename) {
		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		header.add("Cache-Control", "no-cache, no-store, must-revalidate");
		header.add("Pragma", "no-cache");
		header.add("Expires", "0");
		return header;
	}

}
