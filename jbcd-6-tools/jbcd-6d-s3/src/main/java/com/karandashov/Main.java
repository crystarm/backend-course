package com.karandashov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URI;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        String ENDPOINT_URL = System.getenv("ENDPOINT_URL");
        String REGION_NAME = System.getenv("REGION_NAME");
        String ACCESS_KEY = System.getenv("ACCESS_KEY");
        String SECRET_KEY = System.getenv("SECRET_KEY");
        String BUCKET_NAME = System.getenv("BUCKET_NAME");

        if (ENDPOINT_URL == null || REGION_NAME == null || ACCESS_KEY == null || SECRET_KEY == null || BUCKET_NAME == null) {
            log.error("Не заданы переменные окружения!");
            return;
        }

        var s3ClientBuilder = S3Client.builder()
                .endpointOverride(URI.create(ENDPOINT_URL))
                .region(Region.of(REGION_NAME))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)
                ))
                // ВАЖНО: Включаем Path Style Access (http://host:port/bucket/key)
                // Без этого клиент будет стучаться на http://bucket.host:port, что не сработает локально без DNS
                .forcePathStyle(true)
                .serviceConfiguration(S3Configuration.builder()
                        .chunkedEncodingEnabled(false) // Отключаем chunked encoding
                        .build());

        try (S3Client s3 = s3ClientBuilder.build()) {

            // Вывод списка бакетов
            ListBucketsResponse bucketsResponse = s3.listBuckets();
            log.info("Найдены бакеты:");
            bucketsResponse.buckets().forEach(bucket ->
                    log.info(" - {}", bucket.name())
            );

            // Загрузка файла в S3
            String key = "hello-garage.txt";
            String content = "Привет! Это тест записи в Garage S3 из Java";
            s3.putObject(
                    PutObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(key)
                            .build(),
                    RequestBody.fromString(content)
            );
            log.info("Успешно загружено!");

            ResponseBytes<GetObjectResponse> objectBytes = s3.getObject(
                    GetObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(key)
                            .build(),
                    ResponseTransformer.toBytes()
            );
            content = objectBytes.asUtf8String();
            log.info("Содержимое файла из s3: {}", content);

        } catch (S3Exception e) {
            log.error("Ошибка S3: ", e);
        }
    }
}
