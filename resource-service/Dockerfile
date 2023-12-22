# Use a base image with Java 21
FROM openjdk:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the necessary files into the container
COPY target/resource-service-1.0.jar /app/resource-service.jar

# Expose the port the service runs on
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "resource-service.jar"]