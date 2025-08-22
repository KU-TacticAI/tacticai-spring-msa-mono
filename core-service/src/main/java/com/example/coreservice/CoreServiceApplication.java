package com.example.coreservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {
		"com.example.coreservice",
		"com.example.commonmodule"
})
@EntityScan(basePackages = {
		"com.example.coreservice",
		"com.example.commonmodule"
})
@SpringBootApplication(
		scanBasePackages = {
				"com.example.coreservice",
				"com.example.commonmodule"
		}
)
public class CoreServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreServiceApplication.class, args);
	}

}