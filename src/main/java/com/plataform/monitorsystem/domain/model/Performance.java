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

    // Identificador do sistema ou API que está enviando os dados de performance.
    @Column(name = "system_identifier", nullable = false)
    private String systemIdentifier;

    // Pode indicar o endpoint ou a operação monitorada.
    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    // Data e hora do registro.
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // Tempo de resposta em milissegundos.
    @Column(name = "response_time", nullable = false)
    private Long responseTime;

    // Uso de CPU em percentual (ex.: 75.5 para 75,5%).
    @Column(name = "cpu_usage", nullable = false)
    private Double cpuUsage;

    // Uso de memória (em MB ou percentual, conforme a convenção adotada).
    @Column(name = "memory_usage", nullable = false)
    private Double memoryUsage;

    // Taxa de erro, representando a proporção de requisições com erro (valor entre 0 e 1).
    @Column(name = "error_rate")
    private Double errorRate;
}
