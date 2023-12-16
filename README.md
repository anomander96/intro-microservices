

docker-compose exec resource_postgres_db psql -p 5432 -U resource_user -d resource_service_db
docker-compose exec song_postgres_db psql -p 5432 -U song_user -d song_service_db