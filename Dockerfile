# -------- Builder --------
FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app

# Copier le descripteur et résoudre les deps en cache
COPY pom.xml ./
RUN mvn -B -q -e -DskipTests dependency:go-offline

# Copier les sources et builder
COPY src ./src
RUN mvn -B -q -e -DskipTests clean package

# -------- Runtime --------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Installer curl pour healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

# Nom du jar spring-boot à copier (évite le .original)
ARG JAR_FILE=emotionalbook-api-0.1.0-SNAPSHOT.jar

# Copier le jar construit (repackage Spring Boot)
COPY --from=builder /app/target/${JAR_FILE} /app/app.jar

# Port et variables
ENV API_PORT=8080
EXPOSE 8080

# Lancement
ENTRYPOINT ["java","-jar","/app/app.jar"]
