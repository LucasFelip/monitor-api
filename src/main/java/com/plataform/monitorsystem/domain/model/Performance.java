package com.plataform.monitorsystem.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "performance_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "system_identifier", nullable = false)
    private String systemIdentifier;

    @Column(name = "monitored_url", nullable = false)
    private String monitoredUrl;

    @Column(name = "alert_email", nullable = false)
    private String alertEmail;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "response_time", nullable = false)
    private Long responseTime;

    @Column(name = "cpu_usage", nullable = false)
    private Double cpuUsage;

    @Column(name = "memory_usage", nullable = false)
    private Double memoryUsage;

    @Column(name = "error_rate")
    private Double errorRate;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "request_id", unique = true)
    private String requestId;

    @Column(name = "level")
    private String level;

    @Column(name = "details")
    private String details;
}
