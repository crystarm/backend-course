FROM bellsoft/liberica-runtime-container:jre-25-slim-glibc
WORKDIR /app
COPY target/jbcd-6b-kafka-transactional-outbox-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]