package com.plataform.monitorsystem.domain.service;

import com.plataform.monitorsystem.domain.model.Performance;
import com.plataform.monitorsystem.domain.repository.PerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    @Autowired
    public PerformanceService(PerformanceRepository performanceRepository) {
        this.performanceRepository = performanceRepository;
    }

    /**
     * Persiste uma métrica de performance.
     * Se o timestamp não for informado, utiliza o momento atual.
     *
     * @param performance a métrica a ser salva
     * @return a métrica salva
     */
    public Performance savePerformance(Performance performance) {
        if (performance.getTimestamp() == null) {
            performance.setTimestamp(LocalDateTime.now());
        }
        return performanceRepository.save(performance);
    }

    /**
     * Retorna todas as métricas para um determinado sistema (identificado pelo systemIdentifier).
     *
     * @param systemIdentifier identificador do sistema
     * @return lista de métricas
     */
    public List<Performance> getPerformancesBySystemIdentifier(String systemIdentifier) {
        return performanceRepository.findBySystemIdentifier(systemIdentifier);
    }

    /**
     * Retorna métricas dentro de um intervalo de datas.
     *
     * @param start data/hora de início
     * @param end   data/hora de fim
     * @return lista de métricas
     */
    public List<Performance> getPerformancesByDateRange(LocalDateTime start, LocalDateTime end) {
        return performanceRepository.findByTimestampBetween(start, end);
    }

    /**
     * Retorna métricas para um sistema específico dentro de um intervalo de datas.
     *
     * @param systemIdentifier identificador do sistema
     * @param start            data/hora de início
     * @param end              data/hora de fim
     * @return lista de métricas
     */
    public List<Performance> getPerformancesBySystemIdentifierAndDateRange(String systemIdentifier, LocalDateTime start, LocalDateTime end) {
        return performanceRepository.findBySystemIdentifierAndTimestampBetween(systemIdentifier, start, end);
    }
}
