package com.haiqua.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);


		System.out.println("USERNAME = " + System.getenv("DB_USERNAME"));
		System.out.println("PASSWORD = " + System.getenv("DB_PASSWORD"));
	}


}
