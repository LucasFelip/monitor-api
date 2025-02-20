package com.plataform.monitorsystem.core.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AnomalyAlertEvent {
    private String component;
    private String monitorUrl;
    private  String alertEmail;
    private LocalDateTime timestamp;
    private long responseTime;
    private double cpuUsage;
    private double memoryUsage;
    private double errorRate;
    private String requestId;
    private String level;
    private String details;
}
