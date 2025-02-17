```markdown

com.plataform.monitorsystem
├── MonitorSystemApplication.java          // Classe principal da aplicação Spring Boot
├── config                                 // Configurações da aplicação
│   ├── database                         // Configurações dos bancos de dados (JPA, MongoDB)
│   ├── messaging                        // Configurações do Kafka, RabbitMQ, etc.
│   ├── security                         // Configurações do Spring Security e OAuth2 Resource Server
│   └── spark                            // Configurações e integração com Apache Spark
├── controller                           // Endpoints REST para a API
│   ├── PerformanceController.java       // Endpoints para coleta/consulta de métricas de performance
│   └── LogController.java               // Endpoints para coleta/consulta de logs
├── dto                                  // Objetos de Transferência de Dados (para entrada/saída da API)
│   ├── PerformanceDTO.java
│   └── LogDTO.java
├── exception                            // Exceções customizadas e tratamento global de erros
│   └── GlobalExceptionHandler.java
├── model                                // Modelos de domínio (entidades)
│   ├── relational                       // Entidades armazenadas no banco relacional (por exemplo, métricas)
│   │   └── Performance.java
│   └── nosql                            // Entidades para logs e dados de alta frequência (MongoDB)
│       └── Log.java
├── repository                           // Repositórios para persistência dos dados
│   ├── relational                       // Interfaces JPA (ex.: PerformanceRepository)
│   └── nosql                            // Interfaces para MongoDB (ex.: LogRepository)
├── service                              // Camada de serviço (regras de negócio)
│   ├── performance                      // Serviços relacionados às métricas de performance
│   │   └── PerformanceService.java
│   └── log                              // Serviços relacionados aos logs das aplicações
│       └── LogService.java
├── integration                          // Integrações com sistemas externos
│   ├── kafka                            // Componentes para enviar/receber mensagens via Kafka
│   ├── rabbitmq                         // Componentes para integração com RabbitMQ
│   └── spark                            // Jobs ou clientes para interação com o cluster Spark
└── util                                 // Utilitários e classes auxiliares (se necessário)
└── ConverterUtils.java

```
