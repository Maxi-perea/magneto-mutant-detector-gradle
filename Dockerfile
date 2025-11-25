# Build stage
FROM gradle:8.5-jdk21-alpine AS build
WORKDIR /app
COPY mutant-detector .
RUN gradle bootJar --no-daemon -x test

# Run stage
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/mutant-detector-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]