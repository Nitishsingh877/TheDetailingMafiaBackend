FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/Car-service-*.jar app.jar

EXPOSE 8083

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]