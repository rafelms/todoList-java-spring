# Estágio 1: Build (Ambiente de compilação)
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Copia os arquivos do projeto para dentro do container
COPY . .

# Executa o build do Maven gerando o arquivo .jar (pula os testes para acelerar o deploy)
RUN mvn clean package -DskipTests

# Estágio 2: Run (Ambiente de execução mais leve)
FROM eclipse-temurin:21-jdk-jammy

# Porta padrão que o Spring Boot utiliza
EXPOSE 8080

COPY --from=build /target/*.jar app.jar

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]