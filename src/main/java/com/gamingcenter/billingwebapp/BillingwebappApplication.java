package com.gamingcenter.billingwebapp;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SpringBootApplication
public class BillingwebappApplication {

    private static final Logger logger = LoggerFactory.getLogger(BillingwebappApplication.class);


    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        String databaseName = "gaming_center_db"; // Replace with your desired database name
        String jdbcUrl = "jdbc:postgresql://localhost:5432/"; // Connect to default
        String username = "postgres"; // Your PostgreSQL username
        String password = "admin"; // Your PostgreSQL password

        try {
            logger.info("Checking if database '{}' exists...", databaseName);
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(
                    "SELECT count(*) FROM pg_database WHERE datname = '" + databaseName + "'"
            );
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count <= 0) {
                logger.info("Database '{}' does not exist. Creating...", databaseName);
                statement.executeUpdate("CREATE DATABASE " + databaseName);
                logger.info("Database '{}' created successfully.", databaseName);
            } else {
                logger.info("Database '{}' already exists.", databaseName);
            }
        } catch (SQLException e) {
            logger.error("Error during database creation check: {}", e.getMessage());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error("Error closing resources: {}", e.getMessage());
            }
        }
        SpringApplication.run(BillingwebappApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /*// Configure CORS to allow requests from the React frontend.
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000") // In production, replace with your frontend URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }*/

}
