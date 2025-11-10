FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/media-service-*.jar app.jar

EXPOSE 8090

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]