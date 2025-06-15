FROM maven:3.9.9-eclipse-temurin-24-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:24-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/my-api-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT [ "java", "-jar", "app.jar" ]

