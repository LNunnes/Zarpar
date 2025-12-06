package com.zarpar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ZarparApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZarparApplication.class, args);
	}

}
