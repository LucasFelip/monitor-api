package com.plataform.monitorsystem.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailAlertService {
    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);

    @Value("${mail.alert.to}")
    private String alertRecipient;

    @Autowired
    public EmailAlertService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendCriticalAlert(String subject, String body) {
        String formattedBody = formatLogMessage(body);
        sendEmail(alertRecipient, "CRÍTICO: " + subject, formattedBody);
    }

    public void sendCriticalAlert(String email, String subject, String body) {
        String formattedBody = formatLogMessage(body);
        sendEmail(email, "CRÍTICO: " + subject, formattedBody);
    }

    public void sendAlert(String subject, String body) {
        String formattedBody = formatLogMessage(body);
        sendEmail(alertRecipient, subject, formattedBody);
    }

    public void sendAlert(String email, String subject, String body) {
        String formattedBody = formatLogMessage(body);
        sendEmail(email, subject, formattedBody);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }

    /**
     * Tenta converter o JSON recebido em uma mensagem formatada para facilitar a leitura.
     * Caso não seja possível (por exemplo, se o conteúdo não estiver em JSON), retorna o corpo original.
     */
    private String formatLogMessage(String jsonMessage) {
        try {
            JsonNode node = objectMapper.readTree(jsonMessage);

            System.out.println("\n\n\n node: " + node + "\n\n\n");
            StringBuilder sb = new StringBuilder();
            sb.append("====================================\n");
            sb.append("         ALERTA DE SISTEMA          \n");
            sb.append("====================================\n\n");

            sb.append("Resumo do Alerta:\n");
            sb.append("-------------------------------\n");

            if (node.has("component")) {
                sb.append("Componente       : ").append(node.get("component").asText()).append("\n");
            }
            if (node.has("operation")) {
                sb.append("Operação         : ").append(node.get("operation").asText()).append("\n");
            }
            if (node.has("timestamp")) {
                sb.append("Data/Hora        : ").append(node.get("timestamp").asText()).append("\n");
            }
            if (node.has("responseTime")) {
                sb.append("Tempo de Resposta: ").append(node.get("responseTime").asLong()).append(" ms\n");
            }
            if (node.has("cpuUsage")) {
                sb.append("Uso de CPU       : ").append(String.format("%.2f", node.get("cpuUsage").asDouble())).append(" %\n");
            }
            if (node.has("memoryUsage")) {
                sb.append("Uso de Memória   : ").append(String.format("%.2f", node.get("memoryUsage").asDouble())).append(" MB\n");
            }
            if (node.has("level")) {
                sb.append("Nível            : ").append(node.get("level").asText()).append("\n");
            }
            if (node.has("details")) {
                sb.append("Detalhes         : ").append(node.get("details").asText()).append("\n");
            }

            sb.append("-------------------------------\n");
            sb.append("Ação Recomendada : Verificar o sistema imediatamente.\n");
            sb.append("====================================\n");

            return sb.toString();
        } catch (Exception e) {
            return jsonMessage;
        }
    }
}
