package com.project.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.project.security.service"})
@ComponentScan(basePackages = {"com.project.security.repository"})
@ComponentScan(basePackages = {"com.project.security.controller"})
@ComponentScan(basePackages = {"com.project.security.config"})
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

}
