package com.plataform.monitorsystem.domain.service;

import com.plataform.monitorsystem.domain.model.Performance;
import com.plataform.monitorsystem.domain.repository.PerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    @Autowired
    public PerformanceService(PerformanceRepository performanceRepository) {
        this.performanceRepository = performanceRepository;
    }

    public Performance savePerformance(Performance performance) {
        if (performance.getTimestamp() == null) {
            performance.setTimestamp(LocalDateTime.now());
        }
        if (performance.getRequestId() == null || performance.getRequestId().isEmpty()) {
            String requestId = performance.getSystemIdentifier() + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID();
            performance.setRequestId(requestId);
        }
        return performanceRepository.save(performance);
    }

    public List<Performance> getPerformancesBySystemIdentifier(String systemIdentifier) {
        return performanceRepository.findBySystemIdentifier(systemIdentifier);
    }

    public List<Performance> getPerformancesByDateRange(LocalDateTime start, LocalDateTime end) {
        return performanceRepository.findByTimestampBetween(start, end);
    }

    public List<Performance> getPerformancesBySystemIdentifierAndDateRange(String systemIdentifier, LocalDateTime start, LocalDateTime end) {
        return performanceRepository.findBySystemIdentifierAndTimestampBetween(systemIdentifier, start, end);
    }
}
