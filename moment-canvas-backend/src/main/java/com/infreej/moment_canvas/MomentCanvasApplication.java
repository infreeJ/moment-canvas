package com.infreej.moment_canvas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MomentCanvasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MomentCanvasApplication.class, args);
	}

}
