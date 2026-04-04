FROM bellsoft/liberica-runtime-container:jre-21-slim-glibc
WORKDIR /app
COPY target/jbcd-7b-observability-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]