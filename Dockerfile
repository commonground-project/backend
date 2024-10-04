FROM openjdk:21-slim

# Create a group and user
RUN groupadd -r spring && useradd -r -g spring spring

WORKDIR /app
COPY build/libs/*.jar app.jar
USER spring:spring
ENTRYPOINT ["java", "-jar", "/app.jar"]