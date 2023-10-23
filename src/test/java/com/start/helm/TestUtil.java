package com.start.helm;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class TestUtil {

	@SneakyThrows
	public static String inputStreamToString(InputStream is) {
		BufferedInputStream bis = new BufferedInputStream(is);
		byte[] bytes = IOUtils.toByteArray(bis);
		return new String(bytes);
	}

}
