# нужен другой образ из-за использования captcha-библиотеки, которая требует дополнительные пакеты
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache fontconfig ttf-dejavu
WORKDIR /app
COPY target/api-captcha-1.0.0-SNAPSHOT.jar app.jar
# порт 8081 нужен для actuator
EXPOSE 8080 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
