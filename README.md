Right now it's a simple microservice app that can:

Upload a mp3 file, process the file in this way:
Extract file metadata. App is using an external library Apache Tika.
Then app stores mp3 file to the underlying database of the service as Blob(each service
has its own database, docker specified);
App invokes song-service to store song metadata.

Here is a simple instruction for current app running related to module 2
Was updated and added dockerfiles now to start an app you need:
1. Open in terminal the root folder of project intro-microservices.
2. Run: 'docker-compose up --build' for first time or just 'docker-compose up'
3. Open in browser Swagger UI
   http://localhost:8081/swagger-ui.html - this is for resource-service
   http://localhost:8082/swagger-ui.html - this is for song-service

Note: In future should be added full validation, exceptions handling, tests, etc
