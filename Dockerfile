FROM openjdk:21

WORKDIR /app

COPY target/ArtisanConnectBackend-0.0.1-SNAPSHOT.jar app/artisan.jar

ENTRYPOINT ["java","-jar","app/artisan.jar"]