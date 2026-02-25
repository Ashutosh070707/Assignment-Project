package com.example.demo.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jConfig {
    @Value("${NEO4J_URI}")
    private String neo4j_uri;

    @Value("${NEO4J_USERNAME}")
    private String neo4j_username;

    @Value("${NEO4J_PASSWORD}")
    private String neo4j_password;

    @Bean(destroyMethod = "close")
    public Driver neo4jDriver() {
        Driver driver = GraphDatabase.driver(
                neo4j_uri,
                AuthTokens.basic(neo4j_username, neo4j_password)
        );

        driver.verifyConnectivity();
        System.out.println("Database connected successfully");

        return driver;
    }
}
