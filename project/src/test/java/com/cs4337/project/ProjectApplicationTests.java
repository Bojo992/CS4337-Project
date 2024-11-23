package com.cs4337.project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.util.Arrays;

@Testcontainers
@SpringBootTest
class ProjectApplicationTests {

	// Define a MySQLContainer instance
	@Container
	static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest")
			.withDatabaseName("securityDB")            // Set the database name
			.withUsername("root")                // Set the username
			.withPassword("1234")                    // Set the password
			.withCopyFileToContainer(
					MountableFile.forClasspathResource("schema.sql"), // Load schema from resources
					"/docker-entrypoint-initdb.d/schema.sql")         // Place it in MySQL init directory
	;

	// Dynamically set Spring properties to use the Testcontainers instance
	@DynamicPropertySource
	static void configureTestProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", mySQLContainer::getUsername);
		registry.add("spring.datasource.password", mySQLContainer::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
	}

	// Test that the Spring Boot context loads with the containerized MySQL instance
	@Test
	void contextLoads() {
		System.out.println("Test container is running with URL: " + mySQLContainer.getJdbcUrl());
	}
}
