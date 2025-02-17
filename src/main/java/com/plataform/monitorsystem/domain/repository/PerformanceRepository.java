package com.plataform.monitorsystem.domain.repository;

import com.plataform.monitorsystem.domain.model.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    // Busca métricas de performance por identificador do sistema
    List<Performance> findBySystemIdentifier(String systemIdentifier);

    // Busca métricas dentro de um intervalo de datas
    List<Performance> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // Busca métricas por sistema e intervalo de datas
    List<Performance> findBySystemIdentifierAndTimestampBetween(String systemIdentifier, LocalDateTime start, LocalDateTime end);
}
