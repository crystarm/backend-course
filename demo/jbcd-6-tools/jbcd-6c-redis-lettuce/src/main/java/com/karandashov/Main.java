package com.karandashov;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        PropertiesService propertiesService = new PropertiesService();
        String redisHost = propertiesService.get("redis.host");
        String redisPort = propertiesService.get("redis.port");

        try (
                RedisClient redisClient = RedisClient.create(
                        RedisURI.Builder
                                .redis(redisHost, Integer.parseInt(redisPort)) // redis://password@localhost:6379/
                                .build());
                StatefulRedisConnection<String, String> connection = redisClient.connect()
        ) {
            RedisCommands<String, String> syncCommands = connection.sync();
            var redisExample = new RedisExample(syncCommands);
            redisExample.workWithString();
            redisExample.workWithList();
            redisExample.workWithHash();
            redisExample.workWithSet();
            redisExample.workWithZSet();
            redisExample.useIncrCommand();
            redisExample.setKeyWithTTL();
        }
    }
}
