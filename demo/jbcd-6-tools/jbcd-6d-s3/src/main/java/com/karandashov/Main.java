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

        PropertiesService propertiesService = new PropertiesService();
        String endpointUrl = propertiesService.get("s3.endpoint-url");
        String regionName = propertiesService.get("s3.region-name");
        String accessKey = propertiesService.get("s3.access-key");
        String secretKey = propertiesService.get("s3.secret-key");
        String bucketName = propertiesService.get("s3.bucket-name");

        var s3ClientBuilder = S3Client.builder()
                .endpointOverride(URI.create(endpointUrl))
                .region(Region.of(regionName))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
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
                            .bucket(bucketName)
                            .key(key)
                            .build(),
                    RequestBody.fromString(content)
            );
            log.info("Успешно загружено!");

            ResponseBytes<GetObjectResponse> objectBytes = s3.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
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
