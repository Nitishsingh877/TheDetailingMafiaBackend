FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/Discovery-service-*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]