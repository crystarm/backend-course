package com.karandashov;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers(disabledWithoutDocker = true)
public class RedisExampleTest {

    @Container
    @SuppressWarnings("resource")
    private static final GenericContainer<?> REDIS_CONTAINER =
            new GenericContainer<>(DockerImageName.parse("redis:8.6.0"))
                    .withExposedPorts(6379);

    private static RedisClient redisClient;
    private static StatefulRedisConnection<String, String> connection;
    private static RedisCommands<String, String> syncCommands;
    private static RedisExample redisExample;

    @BeforeAll
    static void setUpAll() {
        String redisHost = REDIS_CONTAINER.getHost();
        Integer redisPort = REDIS_CONTAINER.getMappedPort(6379);

        redisClient = RedisClient.create("redis://" + redisHost + ":" + redisPort);
        connection = redisClient.connect();
        syncCommands = connection.sync();
        redisExample = new RedisExample(syncCommands);
    }

    @AfterAll
    static void tearDownAll() {
        if (connection != null) {
            connection.close();
        }
        if (redisClient != null) {
            redisClient.shutdown();
        }
    }

    @BeforeEach
    void setUp() {
        syncCommands.flushall();
    }

    @Test
    void testWorkWithString() {
        redisExample.workWithString();
        assertEquals("hello redis", syncCommands.get("stringKey"));
    }

    @Test
    void testWorkWithList() {
        redisExample.workWithList();
        assertEquals(3, syncCommands.llen("listKey"));
        assertEquals("value3", syncCommands.lindex("listKey", 0));
        assertEquals("value2", syncCommands.lindex("listKey", 1));
        assertEquals("value1", syncCommands.lindex("listKey", 2));
    }

    @Test
    void testWorkWithHash() {
        redisExample.workWithHash();
        Map<String, String> hash = syncCommands.hgetall("hashKey");
        assertEquals(2, hash.size());
        assertEquals("val1", hash.get("field1"));
        assertEquals("val2", hash.get("field2"));
    }

    @Test
    void testWorkWithSet() {
        redisExample.workWithSet();
        assertEquals(2, syncCommands.scard("setKey"));
        assertTrue(syncCommands.sismember("setKey", "member1"));
        assertTrue(syncCommands.sismember("setKey", "member2"));
    }

    @Test
    void testWorkWithZSet() {
        redisExample.workWithZSet();
        assertEquals(3, syncCommands.zcard("zsetKey"));
        assertEquals("zero", syncCommands.zrange("zsetKey", 0, 0).getFirst());
        assertEquals("one", syncCommands.zrange("zsetKey", 1, 1).getFirst());
        assertEquals("two", syncCommands.zrange("zsetKey", 2, 2).getFirst());
    }

    @Test
    void testUseIncrCommand() {
        redisExample.useIncrCommand();
        assertEquals("11", syncCommands.get("counter"));
    }

    @Test
    void testSetKeyWithTTL() {
        redisExample.setKeyWithTTL();
        assertEquals("temporary value", syncCommands.get("ttlKey"));
        assertTrue(syncCommands.ttl("ttlKey") > 0);
    }
}