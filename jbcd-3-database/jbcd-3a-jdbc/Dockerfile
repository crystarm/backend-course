# FROM openjdk:21-jdk-slim - не делайте так! Вам не нужна полноценная JDK всего лишь для запуска
FROM bellsoft/liberica-runtime-container:jre-21-slim-glibc
WORKDIR /app
COPY target/jbcd-3a-jdbc-1.0.0-SNAPSHOT-fat.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]