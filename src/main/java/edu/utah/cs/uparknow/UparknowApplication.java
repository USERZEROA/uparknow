package edu.utah.cs.uparknow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UparknowApplication {

	public static void main(String[] args) {
		SpringApplication.run(UparknowApplication.class, args);
	}
}
