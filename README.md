Right now it's a simple microservice app that can:

Upload a mp3 file, process the file in this way:
Extract file metadata. App is using an external library Apache Tika.
Then resource-service microservice save mp3 file into S3 bucket. Then resource-processor 
microservice parse metadata from mp3 and send it to song-service which save this data into db.
App is using RabbitMQ, Eureka.

Here is a simple instruction for current app version:
1. Open in terminal the root folder of project intro-microservices.
2. Run: 'docker-compose up --build' for first time or just 'docker-compose up'
3. Open in browser Swagger UI

http://localhost:8081/swagger-ui.html - this is for resource-service

http://localhost:8082/swagger-ui.html - this is for song-service

http://localhost:8761 - this is for discovery-service(Eureka)

Application should start all services by one command: 'docker-compose up'
