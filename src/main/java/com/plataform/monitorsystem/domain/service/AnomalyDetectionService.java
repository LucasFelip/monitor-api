package com.plataform.monitorsystem.domain.service;

import com.plataform.monitorsystem.api.dto.LogDTO;
import com.plataform.monitorsystem.api.dto.PerformanceDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AnomalyDetectionService {
    @Value("${anomaly.cpu.critical.threshold:80.0}")
    private double cpuCriticalThreshold;

    @Value("${anomaly.responseTime.critical.threshold:2000}")
    private long responseTimeCriticalThreshold;

    @Value("${anomaly.memory.critical.threshold:8000.0}")
    private double memoryCriticalThreshold;

    @Value("${anomaly.cpu.moderate.threshold:60.0}")
    private double cpuModerateThreshold;

    @Value("${anomaly.responseTime.moderate.threshold:1000}")
    private long responseTimeModerateThreshold;

    @Value("${anomaly.memory.moderate.threshold:6000.0}")
    private double memoryModerateThreshold;

    /**
     * Analisa os dados do monitoramento (LogDTO ou PerformanceDTO) e retorna o resultado da verificação de anomalias.
     */
    public AnomalyAlertResult analyze(Object data) {
        String level;
        String details;
        Double cpuUsage;
        Long responseTime;
        Double memoryUsage;

        if (data instanceof LogDTO) {
            LogDTO logData = (LogDTO) data;
            level = logData.getLevel();
            details = logData.getDetails();
            cpuUsage = logData.getCpuUsage();
            responseTime = logData.getResponseTime();
            memoryUsage = logData.getMemoryUsage();
        } else if (data instanceof PerformanceDTO) {
            PerformanceDTO perfData = (PerformanceDTO) data;
            level = perfData.getLevel();
            details = perfData.getDetails();
            cpuUsage = perfData.getCpuUsage();
            responseTime = perfData.getResponseTime();
            memoryUsage = perfData.getMemoryUsage();
        } else {
            throw new IllegalArgumentException("Tipo de dado não suportado para análise de anomalias.");
        }

        if (!"UP".equalsIgnoreCase(level)) {
            return new AnomalyAlertResult("CRITICAL", "Falha na saúde da API: " + details);
        }

        if (cpuUsage == null || responseTime == null || memoryUsage == null) {
            return new AnomalyAlertResult("CRITICAL", "Dados ausentes: CPU, tempo de resposta ou memória não informados.");
        }

        boolean cpuCritical = cpuUsage > cpuCriticalThreshold;
        boolean responseTimeCritical = responseTime > responseTimeCriticalThreshold;
        boolean memoryCritical = memoryUsage > memoryCriticalThreshold;
        boolean critical = cpuCritical || responseTimeCritical || memoryCritical;

        boolean cpuModerate = cpuUsage > cpuModerateThreshold;
        boolean responseTimeModerate = responseTime > responseTimeModerateThreshold;
        boolean memoryModerate = memoryUsage > memoryModerateThreshold;
        boolean moderate = cpuModerate || responseTimeModerate || memoryModerate;

        if (critical) {
            String msg = "Anomalia CRÍTICA detectada: ";
            if (cpuCritical) { msg += "Uso de CPU elevado; "; }
            if (responseTimeCritical) { msg += "Tempo de resposta elevado; "; }
            if (memoryCritical) { msg += "Uso de memória elevado; "; }
            return new AnomalyAlertResult("CRITICAL", msg);
        } else if (moderate) {
            String msg = "Anomalia MODERADA detectada: ";
            if (cpuModerate) { msg += "Uso de CPU acima do esperado; "; }
            if (responseTimeModerate) { msg += "Tempo de resposta acima do esperado; "; }
            if (memoryModerate) { msg += "Uso de memória acima do esperado; "; }
            return new AnomalyAlertResult("MODERATE", msg);
        } else {
            return new AnomalyAlertResult("NONE", "Sem anomalias detectadas.");
        }
    }

    public static class AnomalyAlertResult {
        private final String level;
        private final String message;

        public AnomalyAlertResult(String level, String message) {
            this.level = level;
            this.message = message;
        }

        public String getLevel() {
            return level;
        }

        public String getMessage() {
            return message;
        }
    }
}
