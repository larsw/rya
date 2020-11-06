package org.apache.rya.web2;

import org.apache.rya.web2.controllers.SparqlController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {

	@Autowired
	SparqlController sparqlController;

	@Test
	void contextLoads() {
		assertThat(sparqlController).isNotNull();
	}
}
