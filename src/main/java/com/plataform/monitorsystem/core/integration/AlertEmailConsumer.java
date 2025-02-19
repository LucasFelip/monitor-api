package com.plataform.monitorsystem.core.integration;

import com.plataform.monitorsystem.core.event.AnomalyAlertEvent;
import com.plataform.monitorsystem.core.event.SelfAnomalyAlertEvent;
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
        this.objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @KafkaListener(topics = "alert-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAlert(String message) {
        try {
            SelfAnomalyAlertEvent alertEvent = objectMapper.readValue(message, SelfAnomalyAlertEvent.class);
            String eventJson = objectMapper.writeValueAsString(alertEvent);

            if ("CRITICAL".equalsIgnoreCase(alertEvent.getLevel())) {
                emailAlertService.sendCriticalAlert("Alerta de Anomalia CRÍTICA", eventJson);
            } else if ("MODERATE".equalsIgnoreCase(alertEvent.getLevel())) {
                emailAlertService.sendAlert("Alerta de Anomalia", eventJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "alert-performance-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAlertPerformance(String message) {
        try {
            AnomalyAlertEvent alertEvent = objectMapper.readValue(message, AnomalyAlertEvent.class);
            String eventJson = objectMapper.writeValueAsString(alertEvent);

            if ("CRITICAL".equalsIgnoreCase(alertEvent.getLevel())) {
                emailAlertService.sendCriticalAlert(alertEvent.getAlertEmail(), "Alerta de Anomalia CRÍTICA", eventJson);
            } else if ("MODERATE".equalsIgnoreCase(alertEvent.getLevel())) {
                emailAlertService.sendAlert(alertEvent.getAlertEmail(),"Alerta de Anomalia", eventJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
