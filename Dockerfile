# Etapa 1: Build da aplicação usando OpenJDK 21 e instalando o Maven
FROM openjdk:17-jdk-slim AS build
RUN apt-get update && apt-get install -y maven
WORKDIR /app
# Copia o pom.xml para aproveitar o cache do Maven
COPY pom.xml .
RUN mvn dependency:go-offline
# Copia o código fonte e realiza o build (pulando os testes, se desejado)
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagem final da aplicação
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/monitor-system-0.0.1-SNAPSHOT.jar app.jar
COPY wait-for-it.sh /app/
RUN chmod +x /app/wait-for-it.sh
EXPOSE 8080
ENTRYPOINT ["/app/wait-for-it.sh", "postgres:5432", "--", "java", "-XX:+UnlockExperimentalVMOptions", "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED", "--add-opens=java.base/sun.security.action=ALL-UNNAMED", "-jar", "app.jar"]