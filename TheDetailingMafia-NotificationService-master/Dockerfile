FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/Notification-service-*.jar app.jar

EXPOSE 8087

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]