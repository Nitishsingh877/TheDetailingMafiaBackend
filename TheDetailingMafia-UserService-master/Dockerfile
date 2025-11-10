FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/User-service-*.jar app.jar

EXPOSE 8081

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]