package com.plataform.monitorsystem.api.controller;

import com.plataform.monitorsystem.api.dto.PerformanceDTO;
import com.plataform.monitorsystem.domain.model.Performance;
import com.plataform.monitorsystem.domain.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private final PerformanceService performanceService;

    @Autowired
    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    /**
     * Endpoint para registrar uma métrica de performance.
     *
     * Exemplo de uso (POST): /api/performance
     * Body (JSON):
     * {
     *   "systemIdentifier": "SistemaExterno1",
     *   "endpoint": "/v1/consulta",
     *   "timestamp": "2025-02-14T12:00:00",
     *   "responseTime": 120,
     *   "cpuUsage": 75.5,
     *   "memoryUsage": 512.0,
     *   "errorRate": 0.05
     * }
     */
    @PostMapping
    public ResponseEntity<PerformanceDTO> createPerformance(@RequestBody PerformanceDTO performanceDTO) {
        // Converter DTO para entidade
        Performance performance = Performance.builder()
                .systemIdentifier(performanceDTO.getSystemIdentifier())
                .endpoint(performanceDTO.getEndpoint())
                .timestamp(performanceDTO.getTimestamp())
                .responseTime(performanceDTO.getResponseTime())
                .cpuUsage(performanceDTO.getCpuUsage())
                .memoryUsage(performanceDTO.getMemoryUsage())
                .errorRate(performanceDTO.getErrorRate())
                .build();

        Performance savedPerformance = performanceService.savePerformance(performance);

        // Converter entidade salva para DTO de resposta
        PerformanceDTO responseDTO = PerformanceDTO.builder()
                .systemIdentifier(savedPerformance.getSystemIdentifier())
                .endpoint(savedPerformance.getEndpoint())
                .timestamp(savedPerformance.getTimestamp())
                .responseTime(savedPerformance.getResponseTime())
                .cpuUsage(savedPerformance.getCpuUsage())
                .memoryUsage(savedPerformance.getMemoryUsage())
                .errorRate(savedPerformance.getErrorRate())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Endpoint para consultar métricas de performance.
     * Permite filtrar por systemIdentifier e/ou por intervalo de datas.
     *
     * Exemplo de uso (GET):
     * /api/performance?systemIdentifier=SistemaExterno1&start=2025-02-14T00:00:00&end=2025-02-14T23:59:59
     */
    @GetMapping
    public ResponseEntity<List<PerformanceDTO>> getPerformances(
            @RequestParam(required = false) String systemIdentifier,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<Performance> performances;
        if (systemIdentifier != null && start != null && end != null) {
            performances = performanceService.getPerformancesBySystemIdentifierAndDateRange(systemIdentifier, start, end);
        } else if (systemIdentifier != null) {
            performances = performanceService.getPerformancesBySystemIdentifier(systemIdentifier);
        } else if (start != null && end != null) {
            performances = performanceService.getPerformancesByDateRange(start, end);
        } else {
            // Caso nenhum filtro seja passado, retorna métricas do último dia como exemplo.
            performances = performanceService.getPerformancesByDateRange(LocalDateTime.now().minusDays(1), LocalDateTime.now());
        }

        List<PerformanceDTO> dtos = performances.stream()
                .map(p -> PerformanceDTO.builder()
                        .systemIdentifier(p.getSystemIdentifier())
                        .endpoint(p.getEndpoint())
                        .timestamp(p.getTimestamp())
                        .responseTime(p.getResponseTime())
                        .cpuUsage(p.getCpuUsage())
                        .memoryUsage(p.getMemoryUsage())
                        .errorRate(p.getErrorRate())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
