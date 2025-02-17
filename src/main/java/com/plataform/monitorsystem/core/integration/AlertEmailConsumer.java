package com.plataform.monitorsystem.core.integration;

import com.plataform.monitorsystem.core.event.AnomalyAlertEvent;
import com.plataform.monitorsystem.domain.service.EmailAlertService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AlertEmailConsumer {

    private final EmailAlertService emailAlertService;
    private final ObjectMapper objectMapper;

    public AlertEmailConsumer(EmailAlertService emailAlertService) {
        this.emailAlertService = emailAlertService;
        // Registra o módulo para o JavaTime (para suportar LocalDateTime)
        this.objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @KafkaListener(topics = "alert-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAlert(String message) {
        try {
            // Converte a mensagem JSON para o objeto de evento
            AnomalyAlertEvent alertEvent = objectMapper.readValue(message, AnomalyAlertEvent.class);
            // Re-serializa o objeto para JSON (ou você pode utilizar o próprio 'message' se ele estiver bem formatado)
            String eventJson = objectMapper.writeValueAsString(alertEvent);

            // Envia o alerta por e-mail utilizando o método que formata a mensagem
            if ("CRITICAL".equalsIgnoreCase(alertEvent.getLevel())) {
                emailAlertService.sendCriticalAlert("Alerta de Anomalia CRÍTICA", eventJson);
            } else if ("MODERATE".equalsIgnoreCase(alertEvent.getLevel())) {
                emailAlertService.sendAlert("Alerta de Anomalia", eventJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
