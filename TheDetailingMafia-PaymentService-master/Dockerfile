FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/Payment-service-*.jar app.jar

EXPOSE 8086

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]