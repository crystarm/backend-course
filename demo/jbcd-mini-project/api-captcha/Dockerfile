FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /builder
COPY target/api-captcha-1.0.0-SNAPSHOT.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM eclipse-temurin:25-jre-alpine
WORKDIR /application
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

RUN apk add --no-cache fontconfig ttf-dejavu

EXPOSE 8080 8081
ENTRYPOINT ["java", "-jar", "application.jar"]
