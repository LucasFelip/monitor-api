# Sistema de Monitoramento de Performance e Logs

## Visão Geral
O **Sistema de Monitoramento de Performance e Logs** é uma solução desenvolvida para monitorar APIs externas e realizar auto-monitoramento de aplicações, identificando anomalias de desempenho automaticamente. Ele analisa métricas como uso de CPU, consumo de memória e tempo de resposta, gerando alertas em tempo real via e-mail.

A aplicação foi desenvolvida utilizando **Spring Boot**, com armazenamento de métricas no **PostgreSQL** e logs no **MongoDB**. A comunicação assíncrona é realizada via **Kafka**, garantindo escalabilidade e alta disponibilidade.

## Arquitetura do Sistema
A arquitetura segue um modelo modular em camadas:
- **Camada de Modelos de Dados**: Representa as entidades do sistema.
    - `Log.java` (MongoDB) → Armazena logs de auto-monitoramento.
    - `Performance.java` (PostgreSQL) → Armazena métricas de desempenho.
- **Camada de Repositórios**: Responsável pela persistência dos dados.
    - `LogRepository.java` → Gerencia logs no MongoDB.
    - `PerformanceRepository.java` → Gerencia métricas no PostgreSQL.
- **Camada de Serviços**: Implementa regras de negócio e processamento.
    - `LogService.java` → Gerencia logs de monitoramento.
    - `PerformanceService.java` → Processa métricas de desempenho.
    - `AnomalyDetectionService.java` → Detecta anomalias.
    - `EmailAlertService.java` → Envia alertas por e-mail.
    - `HealthCheckService.java` → Realiza health-checks.
- **Camada de Comunicação Assíncrona**:
    - `KafkaMessageProducer.java` → Envia eventos para Kafka.
    - `AlertEmailConsumer.java` → Consome mensagens de anomalia.
- **Camada de Monitoramento Agendado**:
    - `SelfMonitoringScheduler.java` → Verifica o próprio sistema.
    - `MonitoringPerformance.java` → Monitora APIs externas.
- **Camada de Controle (Controllers)**:
    - `PerformanceController.java` → Exibe métricas de desempenho.
    - `LogController.java` → Permite consulta de logs.
- **Camada de Configuração**:
    - `SecurityConfig.java` → Define segurança da aplicação.
    - `MailConfig.java` → Configura e-mails para alertas.
    - `KafkaConfig.java` → Gerencia os tópicos Kafka.

## Fluxo de Monitoramento
O monitoramento ocorre de forma contínua e automatizada:
1. `SelfMonitoringScheduler` coleta dados internos (CPU, memória, etc.).
2. `MonitoringPerformance` monitora APIs externas.
3. `AnomalyDetectionService` analisa os dados coletados.
4. Caso haja anomalias, eventos são enviados para **Kafka**.
5. `AlertEmailConsumer` consome as mensagens e aciona **EmailAlertService** para envio de alertas.

## Configuração da Aplicação
As configurações essenciais são definidas nos arquivos `application.properties` e `application-dev.properties`, incluindo:
- **Banco de Dados**:
    - PostgreSQL para métricas.
    - MongoDB para logs.
- **Kafka** para comunicação assíncrona.
- **Serviço de E-mail** para envio de alertas.
- **Thresholds de Detecção de Anomalias**:
    - Uso crítico de CPU acima de 90%.
    - Memória crítica acima de 8000MB.
    - Tempo de resposta crítico acima de 3000ms.

## Deploy com Docker
A aplicação pode ser executada via **Docker Compose**:
```yaml
services:
  postgres:
    image: postgres:latest
    environment:
      POSTGRES_DB: monitor_db
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: secret
    ports:
      - "5432:5432"
  
  mongodb:
    image: mongo:latest
    ports:
      - "27017:27017"
  
  kafka:
    image: wurstmeister/kafka
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    ports:
      - "9092:9092"
  
  monitor-api:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - mongodb
      - kafka
```
Para executar:
```sh
docker-compose up --build
```

## Dependências no pom.xml
Principais dependências do projeto:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

## Conclusão
O **Sistema de Monitoramento de Performance e Logs** é uma solução robusta para detecção automatizada de falhas, garantindo alta disponibilidade e resposta rápida a problemas. A arquitetura modular permite escalabilidade e futuras melhorias, como integração com **Apache Spark** para análise preditiva e autenticação para segurança reforçada.

### Futuras Melhorias:
- Implementação de autenticação para APIs externas.
- Integração com **Apache Spark** para predição de tendências.
- Aprimoramento da escalabilidade com balanceamento de carga.

Este sistema está preparado para se tornar uma referência em monitoramento automatizado, garantindo confiabilidade e eficiência na gestão de APIs e aplicações distribuídas.

