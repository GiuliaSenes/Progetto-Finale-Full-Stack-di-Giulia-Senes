# Dockerfile per pubblicare il backend (per esempio su Render).
# Il percorso di build è la cartella "backend".

# --- 1) Compilazione con Maven ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# --- 2) Immagine finale, solo con Java e il programma ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Il servizio decide la porta con la variabile PORT (vedi server.port in application.properties)
EXPOSE 8080
# MaxRAMPercentage: sui piani gratuiti la memoria è poca, così Java non la supera
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
