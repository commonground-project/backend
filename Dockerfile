FROM openjdk:21-slim

# Create a group and user
RUN groupadd -r spring && useradd -r -g spring spring

WORKDIR /app
COPY build/libs/*.jar /app/app.jar

# Set Spring Boot Actuator endpoint settings in environment variables
ENV SPRINGPROFILES=prod

# Use the user created above
USER spring:spring
EXPOSE 8080

# Health check for the application
HEALTHCHECK --interval=30s --timeout=10s --retries=3 CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
