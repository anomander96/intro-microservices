FROM openjdk:21-jdk

WORKDIR /app

COPY target/api-gateway-1.0.jar /app/api-gateway.jar
COPY entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh
EXPOSE 8989

ENTRYPOINT ["/entrypoint.sh"]