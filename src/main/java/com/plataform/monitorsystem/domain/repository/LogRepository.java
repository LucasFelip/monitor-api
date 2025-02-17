package com.plataform.monitorsystem.domain.repository;

import com.plataform.monitorsystem.domain.model.Log;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogRepository extends MongoRepository<Log, String> {

    // Busca logs por componente responsável pela coleta
    List<Log> findByComponent(String component);

    // Busca logs por nível (ERROR, WARN, INFO, etc.)
    List<Log> findByLevel(String level);

    // Busca logs dentro de um intervalo de datas
    List<Log> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // Busca logs por componente e nível
    List<Log> findByComponentAndLevel(String component, String level);

    // Busca logs por componente e intervalo de datas
    List<Log> findByComponentAndTimestampBetween(String component, LocalDateTime start, LocalDateTime end);
}
