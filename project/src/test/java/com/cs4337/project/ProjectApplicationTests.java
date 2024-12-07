package com.cs4337.project;

import com.cs4337.project.config.KafkaTopicConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@Testcontainers
@RunWith(SpringRunner.class)
@EmbeddedKafka(partitions = 1, topics = { "testTopic" })
@SpringBootTest
class ProjectApplicationTests {


	@Container
	static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.28")
			.withDatabaseName("randomTest")
			.withUsername("test")
			.withPassword("1234")
			.withCopyFileToContainer(
					MountableFile.forClasspathResource("schema.sql"),
					"/docker-entrypoint-initdb.d/schema.sql")
			;

	static {
		mySQLContainer.withDatabaseName("test").start();
	}


	@DynamicPropertySource
	static void configureTestProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", mySQLContainer::getUsername);
		registry.add("spring.datasource.password", mySQLContainer::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
	}


	@Test
	void contextLoads() {
		System.out.println("Test container is running with URL: " + mySQLContainer.getJdbcUrl());
	}
}