package com.plataform.monitorsystem.api.controller;

import com.plataform.monitorsystem.api.dto.PerformanceDTO;
import com.plataform.monitorsystem.core.monitoring.MonitoringPerformance;
import com.plataform.monitorsystem.domain.model.Performance;
import com.plataform.monitorsystem.domain.service.PerformanceService;
import com.plataform.monitorsystem.util.PerformanceConverterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private final PerformanceService performanceService;
    private MonitoringPerformance monitoringPerformance;

    @Autowired
    public PerformanceController(PerformanceService performanceService, MonitoringPerformance monitoringPerformance) {
        this.performanceService = performanceService;
        this.monitoringPerformance = monitoringPerformance;
    }

    @PostMapping("/external")
    public ResponseEntity<String> monitorExternalPerformance(@RequestBody PerformanceDTO performanceDTO) {
        monitoringPerformance.monitor(performanceDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Realizando monitoramento de performance externo.");
    }

    @GetMapping("/system/{systemIdentifier}")
    public ResponseEntity<List<PerformanceDTO>> getPerformancesBySystem(@PathVariable String systemIdentifier) {
        List<Performance> performances = performanceService.getPerformancesBySystemIdentifier(systemIdentifier);
        List<PerformanceDTO> performanceDTOs = performances.stream()
                .map(PerformanceConverterUtil::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(performanceDTOs);
    }

}
