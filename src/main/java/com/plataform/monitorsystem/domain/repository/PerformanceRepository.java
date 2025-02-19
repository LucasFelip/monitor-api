package com.plataform.monitorsystem.domain.repository;

import com.plataform.monitorsystem.domain.model.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    List<Performance> findBySystemIdentifier(String systemIdentifier);

    List<Performance> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<Performance> findBySystemIdentifierAndTimestampBetween(String systemIdentifier, LocalDateTime start, LocalDateTime end);

    Performance findByRequestId(String requestId);
}
