FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/review-service-*.jar app.jar

EXPOSE 8092

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]