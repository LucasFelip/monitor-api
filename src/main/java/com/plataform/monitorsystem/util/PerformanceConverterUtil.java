package com.plataform.monitorsystem.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.plataform.monitorsystem.api.dto.PerformanceDTO;
import com.plataform.monitorsystem.domain.model.Performance;

public class PerformanceConverterUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static Performance convertToEntity(PerformanceDTO dto) {
        return Performance.builder()
                .systemIdentifier(dto.getSystemIdentifier())
                .requestId(dto.getRequestId())
                .alertEmail(dto.getAlertEmail())
                .timestamp(dto.getTimestamp())
                .monitoredUrl(dto.getMonitoredUrl())
                .responseTime(dto.getResponseTime())
                .level(dto.getLevel())
                .details(dto.getDetails())
                .build();
    }

    public static String convertToJson(PerformanceDTO dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter PerformanceDTO para JSON", e);
        }
    }

    public static PerformanceDTO convertToDto(Performance entity) {
        return PerformanceDTO.builder()
                .systemIdentifier(entity.getSystemIdentifier())
                .monitoredUrl(entity.getMonitoredUrl())
                .alertEmail(entity.getAlertEmail())
                .timestamp(entity.getTimestamp())
                .responseTime(entity.getResponseTime())
                .cpuUsage(entity.getCpuUsage())
                .memoryUsage(entity.getMemoryUsage())
                .errorRate(entity.getErrorRate())
                .requestId(entity.getRequestId())
                .level(entity.getLevel())
                .details(entity.getDetails())
                .build();
    }
}
