package com.mls.workflow.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializerConfig implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Create the AIC_CADASTRO table if it doesn't exist
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS AIC_CADASTRO (
                ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                NOME VARCHAR(255) NOT NULL,
                EMAIL VARCHAR(255) NOT NULL,
                IDADE INT
            )
            """;
        
        jdbcTemplate.execute(createTableSql);
        System.out.println("Table AIC_CADASTRO created successfully");
    }
}