package com.plataform.monitorsystem.core.monitoring;

import com.plataform.monitorsystem.api.dto.LogDTO;
import com.plataform.monitorsystem.core.integration.KafkaMessageProducer;
import com.plataform.monitorsystem.domain.model.Log;
import com.plataform.monitorsystem.domain.service.AnomalyDetectionService;
import com.plataform.monitorsystem.domain.service.HealthCheckService;
import com.plataform.monitorsystem.domain.service.LogService;
import com.plataform.monitorsystem.util.LogConverterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;

@Component
public class SelfMonitoringScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SelfMonitoringScheduler.class);

    private final LogService logService;
    private final KafkaMessageProducer kafkaProducer;
    private final HealthCheckService healthCheckService;
    private final AnomalyDetectionService anomalyDetectionService;

    private final String healthCheckUrl = "http://localhost:8080/actuator/health";

    @Autowired
    public SelfMonitoringScheduler(LogService logService,
                                   KafkaMessageProducer kafkaProducer,
                                   HealthCheckService healthCheckService,
                                   AnomalyDetectionService anomalyDetectionService) {
        this.logService = logService;
        this.kafkaProducer = kafkaProducer;
        this.healthCheckService = healthCheckService;
        this.anomalyDetectionService = anomalyDetectionService;
    }

    @Scheduled(fixedDelayString = "${self.monitor.interval:60000}")
    public void performSelfMonitoring() {
        try {
            long startTime = System.currentTimeMillis();
            boolean healthy;
            String healthErrorMessage = null;
            try {
                healthy = healthCheckService.checkApiHealth(healthCheckUrl);
            } catch (Exception ex) {
                healthy = false;
                healthErrorMessage = ex.getMessage();
                logger.error("Exceção no health check: {}", healthErrorMessage, ex);
            }
            long responseTime = System.currentTimeMillis() - startTime;

            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            double cpuUsage = osBean.getSystemCpuLoad() * 100;
            double memoryUsage = (osBean.getTotalPhysicalMemorySize() - osBean.getFreePhysicalMemorySize())
                    / (1024.0 * 1024.0);

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

            LogDTO logDTO = LogDTO.builder()
                    .component("SelfMonitor")
                    .operation("HealthCheck")
                    .timestamp(LocalDateTime.now())
                    .responseTime(responseTime)
                    .cpuUsage(cpuUsage)
                    .memoryUsage(memoryUsage)
                    .level(level)
                    .details(details)
                    .build();

            AnomalyDetectionService.AnomalyAlertResult alertResult = anomalyDetectionService.analyze(logDTO);

            if (!"NONE".equalsIgnoreCase(alertResult.getLevel())) {
                logDTO.setLevel(alertResult.getLevel());
                logDTO.setDetails(logDTO.getDetails() + "\n" + alertResult.getMessage());
            }

            Log logEntity = LogConverterUtil.convertToEntity(logDTO);
            logService.saveLog(logEntity);

            if (!"NONE".equalsIgnoreCase(alertResult.getLevel())) {
                String jsonMessage = LogConverterUtil.convertToJson(logDTO);
                kafkaProducer.sendMessage("alert-topic", jsonMessage);
            }

            logger.info("Self-monitoring executado: healthy={}, responseTime={}ms, cpuUsage={}%, memoryUsage={}MB",
                    healthy, responseTime, cpuUsage, memoryUsage);
        } catch (Exception e) {
            logger.error("Erro durante o auto-monitoramento", e);
        }
    }
}
