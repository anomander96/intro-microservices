FROM openjdk:21-jdk

WORKDIR /app

COPY target/song-service-1.0.jar /app/song-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "song-service.jar"]