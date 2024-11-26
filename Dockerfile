FROM openjdk:21-slim

# Create a group and user
RUN groupadd -r spring && useradd -r -g spring spring

RUN apt-get update && apt-get install -y curl
RUN curl https://www.google.com/

WORKDIR /app
COPY build/libs/*.jar /app/app.jar

# Set Spring Boot Actuator endpoint settings in environment variables
ENV SPRINGPROFILES=prod

# Use the user created above
USER spring:spring
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
