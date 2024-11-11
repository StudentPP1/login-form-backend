FROM maven:3.8-openjdk-17 as maven-builder
COPY src /app/src
COPY pom.xml /app

RUN mvn -f /app/pom.xml clean package -DskipTests
FROM openjdk:17-alpine

COPY --from=maven-builder app/target/advanced-login-form-0.0.1-SNAPSHOT.jar /app-service/advanced-login-form-0.0.1-SNAPSHOT.jar
WORKDIR /app-service

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "advanced-login-form-0.0.1-SNAPSHOT.jar"]