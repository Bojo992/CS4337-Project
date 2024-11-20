package com.project.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@Testcontainers
@SpringBootTest
class SecurityApplicationTests {
	@Container
	static final MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest");

	static {
		mySQLContainer.withDatabaseName("test")
				.withUsername("root")
				.withPassword("")
				.withCopyFileToContainer(
						MountableFile.forClasspathResource("schema.sql"),
						"/docker-entrypoint-initdb.d/schema.sql")
				.start();
	}

	@DynamicPropertySource
	static void configureTestProperties(DynamicPropertyRegistry registry){
		registry.add("spring.datasource.url",() -> mySQLContainer.getJdbcUrl());
		registry.add("spring.datasource.username",() -> mySQLContainer.getUsername());
		registry.add("spring.datasource.password",() -> mySQLContainer.getPassword());
		registry.add("spring.jpa.hibernate.ddl-auto",() -> "create");
	}


	@Test
	void contextLoads() {
	}

}
