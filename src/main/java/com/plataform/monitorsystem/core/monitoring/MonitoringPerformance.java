package com.plataform.monitorsystem.core.monitoring;

import com.plataform.monitorsystem.api.dto.PerformanceDTO;
import com.plataform.monitorsystem.core.integration.KafkaMessageProducer;
import com.plataform.monitorsystem.domain.model.Performance;
import com.plataform.monitorsystem.domain.service.AnomalyDetectionService;
import com.plataform.monitorsystem.domain.service.HealthCheckService;
import com.plataform.monitorsystem.domain.service.PerformanceService;
import com.plataform.monitorsystem.util.PerformanceConverterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MonitoringPerformance {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringPerformance.class);

    private final KafkaMessageProducer kafkaProducer;
    private final HealthCheckService healthCheckService;
    private final AnomalyDetectionService anomalyDetectionService;
    private final PerformanceService performanceService;

    @Autowired
    public MonitoringPerformance(KafkaMessageProducer kafkaProducer, HealthCheckService healthCheckService, AnomalyDetectionService anomalyDetectionService, PerformanceService performanceService) {
        this.kafkaProducer = kafkaProducer;
        this.healthCheckService = healthCheckService;
        this.anomalyDetectionService = anomalyDetectionService;
        this.performanceService = performanceService;
    }

    public void  monitor(PerformanceDTO dto) {
        try {
            String requestId = dto.getSystemIdentifier() + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID();
            dto.setRequestId(requestId);
            dto.setTimestamp(LocalDateTime.now());

            long startTime = System.currentTimeMillis();
            boolean healthy;
            String healthErrorMessage = null;
            try {
                healthy = healthCheckService.checkApiHealth(dto.getMonitoredUrl());
            } catch (Exception ex) {
                healthy = false;
                healthErrorMessage = ex.getMessage();
                logger.error("Exceção no health check: {}", healthErrorMessage, ex);
            }
            long responseTime = System.currentTimeMillis() - startTime;

            // Define os valores do status de forma consistente
            String level;
            String details;
            if (healthy) {
                level = "UP";  // Valor padrão para indicar que a API está saudável
                details = "API respondendo normalmente.";
            } else {
                level = "DOWN";
                details = "Não foi possível verificar a saúde da API.";
                if (healthErrorMessage != null) {
                    details += " Erro: " + healthErrorMessage;
                }
            }

            dto.setLevel(level);
            dto.setDetails(details);
            dto.setResponseTime(responseTime);

            AnomalyDetectionService.AnomalyAlertResult alertResult = anomalyDetectionService.analyze(dto);

            if(!"NONE".equalsIgnoreCase(alertResult.getLevel())) {
                dto.setLevel(alertResult.getLevel());
                dto.setDetails(dto.getDetails() + "\n"+ alertResult.getMessage());
            }

            Performance performanceEntity = PerformanceConverterUtil.convertToEntity(dto);
            performanceService.savePerformance(performanceEntity);


            if(!"NONE".equalsIgnoreCase(alertResult.getLevel())) {
                String jsonMessage = PerformanceConverterUtil.convertToJson(dto);
                kafkaProducer.sendMessage("alert-performance-topic",jsonMessage);
            }

            logger.info("Monitoring executado: healthy={}, responseTime={}ms, cpuUsage={}%, memoryUsage={}MB",
                    healthy, responseTime, dto.getCpuUsage(),dto.getMemoryUsage());
        } catch (Exception e) {
            logger.error("Erro durante o auto-monitoramento", e);
        }
    }
}
