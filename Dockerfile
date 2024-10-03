FROM openjdk:21
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /work/src
COPY build/libs/*.jar app.jar
USER spring:spring
ENTRYPOINT ["java","-jar","/app.jar"]