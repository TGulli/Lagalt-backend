package com.noroff.lagalt;

import com.noroff.lagalt.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class LagaltApplication {

	public static void main(String[] args) {
		SpringApplication.run(LagaltApplication.class, args);
	}

}
