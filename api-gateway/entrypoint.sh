#!/bin/sh

echo "Starting entrypoint script..."

echo "Sleeping for 30 seconds before starting the service..."
sleep 30

echo "Starting service..."
exec java -jar /app/api-gateway.jar