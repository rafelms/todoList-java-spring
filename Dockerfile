# Estágio 1: Build da aplicação usando Maven e Java 17 ou 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Estágio 2: Execução da aplicação (imagem mais leve)
FROM eclipse-temurin:21-jdk-jammy
EXPOSE 8080
COPY --from=build /target/todolist-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]