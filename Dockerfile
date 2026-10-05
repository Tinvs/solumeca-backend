# Stage 1: Build con Maven y Java 21
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Descarga de dependencias en cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compilación y empaquetado del JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Imagen liviana de ejecución
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia del ejecutable generado
COPY --from=build /app/target/solumeca-backend-1.0.0.jar app.jar

# Puerto dinámico inyectado por Render ($PORT)
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
