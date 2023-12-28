FROM openjdk:21-jdk

WORKDIR /app

COPY target/resource-service-1.0.jar /app/resource-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "resource-service.jar"]