package com.plataform.monitorsystem.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDTO {
    private String systemIdentifier;
    private String endpoint;
    private LocalDateTime timestamp;
    private Long responseTime;
    private Double cpuUsage;
    private Double memoryUsage;
    private Double errorRate;
}
