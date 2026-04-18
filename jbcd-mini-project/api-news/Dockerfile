FROM bellsoft/liberica-runtime-container:jre-21-slim-glibc
WORKDIR /app
COPY target/api-news-1.0.0-SNAPSHOT.jar app.jar
# порт 8081 нужен для actuator
EXPOSE 8080 8081
ENTRYPOINT ["java", "-jar", "app.jar"]