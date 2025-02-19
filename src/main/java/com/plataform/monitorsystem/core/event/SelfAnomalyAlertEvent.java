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
public class SelfAnomalyAlertEvent {
    private String component;
    private String operation;
    private LocalDateTime timestamp;
    private long responseTime;
    private double cpuUsage;
    private double memoryUsage;
    private String level;
    private String details;
}
