package com.codingonapotato.illuminactivity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling 
@SpringBootApplication
public class IlluminactivityApplication {

	public static void main(String[] args) {
		SpringApplication.run(IlluminactivityApplication.class, args);
	}

}
