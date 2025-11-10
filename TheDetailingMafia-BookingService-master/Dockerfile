FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/Booking-service-*.jar app.jar

EXPOSE 8084

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]