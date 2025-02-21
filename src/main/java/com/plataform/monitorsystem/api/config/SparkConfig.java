package com.plataform.monitorsystem.api.config;

import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Value("${spark.master.url}")
    private String sparkMaster;

    @Value("${spark.app.name}")
    private String sparkAppName;

    @Bean
    public SparkSession sparkSession() {
        return SparkSession.builder()
                .master(sparkMaster)
                .appName(sparkAppName)
                .config("spark.driver.host", "localhost")
                .config("spark.sql.catalogImplementation", "in-memory")
                .getOrCreate();
    }
}
