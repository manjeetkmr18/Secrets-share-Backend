package com.OnePassLink.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.OnePassLink.backend")
public class OnePassLinkBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnePassLinkBackendApplication.class, args);
	}

}
