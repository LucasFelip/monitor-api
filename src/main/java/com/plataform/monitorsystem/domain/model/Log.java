package com.plataform.monitorsystem.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "internal_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Log {

    @Id
    private String id;

    // Indica o componente responsável pela coleta (ex.: "SelfMonitor", "HealthChecker", "Scheduler")
    private String component;

    // Nome ou descrição da operação monitorada (ex.: "GET /actuator/health", "Scheduled HealthCheck")
    private String operation;

    // Data e hora do registro
    private LocalDateTime timestamp;

    // Tempo de resposta medido em milissegundos para a operação monitorada
    private Long responseTime;

    // Uso de CPU no momento da coleta (em percentual)
    private Double cpuUsage;

    // Uso de memória no momento da coleta (por exemplo, em MB)
    private Double memoryUsage;

    // Nível do log (por exemplo, INFO se tudo estiver ok, ou ERROR se alguma anomalia for detectada)
    private String level;

    // Detalhes adicionais ou mensagens de erro, se houver
    private String details;
}
