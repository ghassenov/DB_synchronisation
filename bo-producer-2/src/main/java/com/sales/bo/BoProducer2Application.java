package com.sales.bo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BoProducer2Application {

	public static void main(String[] args) {
		SpringApplication.run(BoProducer2Application.class, args);
	}

}
