package com.plataform.monitorsystem.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogDTO {
    private String component;
    private String operation;
    private LocalDateTime timestamp;
    private Long responseTime;
    private Double cpuUsage;
    private Double memoryUsage;
    private String level;
    private String details;
}
