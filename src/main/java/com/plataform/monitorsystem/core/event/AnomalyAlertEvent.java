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
    private String operation;
    private LocalDateTime timestamp;
    private long responseTime;
    private double cpuUsage;
    private double memoryUsage;
    private String level;
    private String details;

    @Override
    public String toString() {
        return "----- Anomaly Alert Event -----\n" +
                "Componente: " + component + "\n" +
                "Operação: " + operation + "\n" +
                "Data/Hora: " + timestamp + "\n" +
                "Tempo de Resposta: " + responseTime + " ms\n" +
                "Uso de CPU: " + cpuUsage + " %\n" +
                "Uso de Memória: " + memoryUsage + " MB\n" +
                "Nível: " + level + "\n" +
                "Detalhes: " + details + "\n" +
                "-------------------------------\n";
    }
}
