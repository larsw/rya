package org.apache.rya.web2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class RyaWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(RyaWebApplication.class, args);
	}
}
