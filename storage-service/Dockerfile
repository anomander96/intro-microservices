FROM openjdk:21-jdk

WORKDIR /app

COPY target/storage-service-1.0.jar /app/storage-service.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "storage-service.jar"]