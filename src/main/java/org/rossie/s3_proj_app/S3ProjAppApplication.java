package org.rossie.s3_proj_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.rossie.s3_proj_app"})
public class S3ProjAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(S3ProjAppApplication.class, args);
	}
}
