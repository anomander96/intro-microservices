# Use a base image with Java 21
FROM openjdk:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the necessary files into the container
COPY target/song-service.jar /app/song-service.jar

# Expose the port the service runs on
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "song-service.jar"]