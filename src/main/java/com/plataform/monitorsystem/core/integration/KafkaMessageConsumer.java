package com.plataform.monitorsystem.core.integration;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageConsumer {

    /**
     * Método que escuta mensagens do tópico "monitor-system-topic".
     * O groupId é definido nas propriedades do Spring.
     *
     * @param message a mensagem recebida
     */
    @KafkaListener(topics = "monitor-system-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        // Processa a mensagem recebida
        System.out.println("Received Kafka message: " + message);
    }
}
