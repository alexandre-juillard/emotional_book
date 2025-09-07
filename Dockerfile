# Build & run minimal API Java
# JDK 21 requis pour les threads virtuels utilisés dans Main.java
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Version du driver MySQL
ARG MYSQL_JDBC_VERSION=8.4.0

# Télécharger le driver MySQL Connector/J depuis Maven Central
ADD https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/${MYSQL_JDBC_VERSION}/mysql-connector-j-${MYSQL_JDBC_VERSION}.jar /app/lib/mysql-connector-j.jar

# Copier les sources
COPY src ./src

# Compiler
RUN javac -d out src/*.java

# Config
ENV API_PORT=8080
EXPOSE 8080

# Lancer l’API avec le driver MySQL dans le classpath
CMD ["sh","-c","java -cp out:/app/lib/mysql-connector-j.jar Main"]
