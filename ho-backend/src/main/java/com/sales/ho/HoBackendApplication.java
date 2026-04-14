package com.sales.ho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@SpringBootApplication
@EnableRabbit
public class HoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoBackendApplication.class, args);
	}

}
