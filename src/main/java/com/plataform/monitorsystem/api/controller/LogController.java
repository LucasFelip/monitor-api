package com.plataform.monitorsystem.api.controller;

import com.plataform.monitorsystem.api.dto.LogDTO;
import com.plataform.monitorsystem.domain.model.Log;
import com.plataform.monitorsystem.domain.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    /**
     * Endpoint para consultar logs de auto-monitoramento.
     * Permite filtrar por componente, nível e/ou intervalo de datas.
     *
     * Exemplo de uso (GET):
     * /api/logs?component=SelfMonitor&level=INFO&start=2025-02-14T00:00:00&end=2025-02-14T23:59:59
     */
    @GetMapping
    public ResponseEntity<List<LogDTO>> getLogs(
            @RequestParam(required = false) String component,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<Log> logs;
        if (component != null && level != null) {
            logs = logService.getLogsByComponentAndLevel(component, level);
        } else if (component != null && start != null && end != null) {
            logs = logService.getLogsByComponentAndDateRange(component, start, end);
        } else if (component != null) {
            logs = logService.getLogsByComponent(component);
        } else if (level != null) {
            logs = logService.getLogsByLevel(level);
        } else if (start != null && end != null) {
            logs = logService.getLogsByDateRange(start, end);
        } else {
            // Caso nenhum filtro seja passado, retorna logs do último dia como exemplo.
            logs = logService.getLogsByDateRange(LocalDateTime.now().minusDays(1), LocalDateTime.now());
        }

        List<LogDTO> dtos = logs.stream()
                .map(l -> LogDTO.builder()
                        .component(l.getComponent())
                        .operation(l.getOperation())
                        .timestamp(l.getTimestamp())
                        .responseTime(l.getResponseTime())
                        .cpuUsage(l.getCpuUsage())
                        .memoryUsage(l.getMemoryUsage())
                        .level(l.getLevel())
                        .details(l.getDetails())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
