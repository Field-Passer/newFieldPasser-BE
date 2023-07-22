package com.example.newfieldpasser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NewFieldPasserApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewFieldPasserApplication.class, args);
	}

}
