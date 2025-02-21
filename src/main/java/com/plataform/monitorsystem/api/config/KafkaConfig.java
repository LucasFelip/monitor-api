package com.plataform.monitorsystem.api.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.consumer.group-id}")

    @Bean
    public NewTopic alertTopic() {
        return new NewTopic("alert-topic", 1, (short) 1);
    }

    @Bean
    public NewTopic performanceTopic() {
        return new NewTopic("performance-topic", 1, (short) 1);
    }

    @Bean
    public NewTopic alertPerformanceTopic() {
        return new NewTopic("alert-performance-topic", 1, (short) 1);
    }

    @Bean
    public NewTopic predictionTopic() {
        return new NewTopic("prediction-topic", 1, (short) 1);
    }
}
