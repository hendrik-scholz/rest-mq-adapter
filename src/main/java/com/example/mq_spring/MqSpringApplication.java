package com.example.mq_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class MqSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(MqSpringApplication.class, args);
	}
}
