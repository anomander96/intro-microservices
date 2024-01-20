FROM openjdk:21-jdk

WORKDIR /app

COPY target/resource-processor-1.0.jar /app/resource-processor.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "resource-processor.jar"]