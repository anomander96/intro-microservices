FROM openjdk:21-jdk

WORKDIR /app

COPY target/discovery-service-1.0.jar /app/discovery-service.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app/discovery-service.jar"]