Right now it's a simple microservice app that can:

Upload a mp3 file, process the file in this way:
Extract file metadata. App is using an external library Apache Tika.
Then app stores mp3 file to the underlying database of the service as Blob(each service
has its own database, docker specified);
App invokes song-service to store song metadata.

Here is a simple instruction for current app running:
1. Open in terminal the root folder of project intro-microservices.
2. Run: 'docker-compose up -d' / this command will start two db images for each service
You can connect to each db using this commands:
docker-compose exec resource_postgres_db psql -p 5432 -U resource_user -d resource_service_db
docker-compose exec song_postgres_db psql -p 5432 -U song_user -d song_service_db
3. Then run two spring boot apps: ResourceServiceApplication and SongServiceApplication
4. Open in browser Swagger UI
   http://localhost:8081/swagger-ui.html - this is for resource-service
   http://localhost:8082/swagger-ui.html - this is for song-service

Note: In future should be added full validation, exceptions handling, tests, etc
