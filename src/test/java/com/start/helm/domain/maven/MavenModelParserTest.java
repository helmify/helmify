package com.start.helm.domain.maven;

import com.start.helm.TestUtil;
import org.apache.maven.api.model.Model;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MavenModelParserTest {

	@Test
	void parsePom() throws IOException {

		MockMultipartFile mock = new MockMultipartFile("pom-with-rabbit.xml", "pom-with-rabbit.xml", "text/plain",
				getClass().getClassLoader().getResourceAsStream("pom-with-rabbit.xml"));

		Optional<Model> model = MavenModelParser.parsePom(TestUtil.inputStreamToString(mock.getInputStream()));
		assertTrue(model.isPresent());

		mock = new MockMultipartFile("pom-invalid.xml", "pom-invalid.xml", "text/plain",
				getClass().getClassLoader().getResourceAsStream("pom-invalid.xml"));
		model = MavenModelParser.parsePom(TestUtil.inputStreamToString(mock.getInputStream()));
		assertFalse(model.isPresent());

	}

}
