FROM eclipse-temurin:21

# Create a group and user
RUN groupadd -r spring && useradd -r -g spring spring

RUN apt-get update && apt-get install -y jq:latest

WORKDIR /app
COPY build/libs/*.jar /app/app.jar

# Set Spring Boot Actuator endpoint settings in environment variables
ENV SPRINGPROFILES=prod

# Use the user created above
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
