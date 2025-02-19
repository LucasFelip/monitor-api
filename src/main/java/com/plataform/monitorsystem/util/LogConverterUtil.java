package com.plataform.monitorsystem.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plataform.monitorsystem.api.dto.LogDTO;
import com.plataform.monitorsystem.domain.model.Log;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class LogConverterUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static Log convertToEntity(LogDTO dto) {
        return Log.builder()
                .component(dto.getComponent())
                .operation(dto.getOperation())
                .timestamp(dto.getTimestamp())
                .responseTime(dto.getResponseTime())
                .cpuUsage(dto.getCpuUsage())
                .memoryUsage(dto.getMemoryUsage())
                .level(dto.getLevel())
                .details(dto.getDetails())
                .build();
    }

    public static String convertToJson(LogDTO dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter LogDTO para JSON", e);
        }
    }

    public static LogDTO convertToDto(Log entity) {
        return LogDTO.builder()
                .component(entity.getComponent())
                .operation(entity.getOperation())
                .timestamp(entity.getTimestamp())
                .responseTime(entity.getResponseTime())
                .cpuUsage(entity.getCpuUsage())
                .memoryUsage(entity.getMemoryUsage())
                .level(entity.getLevel())
                .details(entity.getDetails())
                .build();
    }
}
