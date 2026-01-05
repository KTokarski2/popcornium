package com.teg.popcornium_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
@EnableNeo4jRepositories(basePackages = "com.teg.popcornium_api.common.neo4jrepository")
public class PopcorniumApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PopcorniumApiApplication.class, args);
	}
}
