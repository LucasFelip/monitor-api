package com.plataform.monitorsystem.domain.service;

import com.plataform.monitorsystem.domain.model.Log;
import com.plataform.monitorsystem.domain.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogService {

    private final LogRepository logRepository;

    @Autowired
    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    /**
     * Persiste um log de auto-monitoramento.
     * Se o timestamp não for informado, utiliza o momento atual.
     *
     * @param log o log a ser salvo
     * @return o log salvo
     */
    public Log saveLog(Log log) {
        if (log.getTimestamp() == null) {
            log.setTimestamp(LocalDateTime.now());
        }
        return logRepository.save(log);
    }

    /**
     * Retorna logs por componente.
     *
     * @param component nome do componente (ex.: "SelfMonitor")
     * @return lista de logs
     */
    public List<Log> getLogsByComponent(String component) {
        return logRepository.findByComponent(component);
    }

    /**
     * Retorna logs por nível.
     *
     * @param level nível do log (ex.: ERROR, INFO)
     * @return lista de logs
     */
    public List<Log> getLogsByLevel(String level) {
        return logRepository.findByLevel(level);
    }

    /**
     * Retorna logs dentro de um intervalo de datas.
     *
     * @param start data/hora de início
     * @param end   data/hora de fim
     * @return lista de logs
     */
    public List<Log> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return logRepository.findByTimestampBetween(start, end);
    }

    /**
     * Retorna logs por componente e nível.
     *
     * @param component nome do componente
     * @param level     nível do log
     * @return lista de logs
     */
    public List<Log> getLogsByComponentAndLevel(String component, String level) {
        return logRepository.findByComponentAndLevel(component, level);
    }

    /**
     * Retorna logs por componente dentro de um intervalo de datas.
     *
     * @param component nome do componente
     * @param start     data/hora de início
     * @param end       data/hora de fim
     * @return lista de logs
     */
    public List<Log> getLogsByComponentAndDateRange(String component, LocalDateTime start, LocalDateTime end) {
        return logRepository.findByComponentAndTimestampBetween(component, start, end);
    }

    public List<Log> findAllLogs() {
        return logRepository.findAll();
    }
}
