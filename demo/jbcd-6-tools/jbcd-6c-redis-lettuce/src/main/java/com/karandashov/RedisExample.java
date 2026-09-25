package com.karandashov;

import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisExample {

    private static final Logger log = LoggerFactory.getLogger(RedisExample.class);

    private final RedisCommands<String, String> redisCommands;

    public RedisExample(RedisCommands<String, String> redisCommands) {
        this.redisCommands = redisCommands;
    }

    // Работа с типом String
    public void workWithString() {
        log.info("--- Работа с типом String ---");
        String setRes = redisCommands.set("stringKey", "hello redis");
        log.info("Установлен ключ stringKey: {}", setRes);
        String val = redisCommands.get("stringKey");
        log.info("Получено значение для stringKey: {}", val);
    }

    // Работа с типом List
    public void workWithList() {
        log.info("--- Работа с типом List ---");
        redisCommands.del("listKey");
        Long len = redisCommands.lpush("listKey", "value1", "value2", "value3");
        log.info("Добавлены элементы в список listKey, новая длина: {}", len);
        redisCommands.lrange("listKey", 0, -1).forEach(val -> log.info("Элемент списка: {}", val));
    }

    // Работа с типом Hash
    public void workWithHash() {
        log.info("--- Работа с типом Hash ---");
        redisCommands.hset("hashKey", "field1", "val1");
        log.info("Поле field1 установлено в Hash");
        redisCommands.hset("hashKey", "field2", "val2");
        redisCommands.hgetall("hashKey").forEach((k, v) -> log.info("Поле: {}, значение: {}", k, v));
    }

    // Работа с типом Set
    public void workWithSet() {
        log.info("--- Работа с типом Set ---");
        Long count = redisCommands.sadd("setKey", "member1", "member2", "member1");
        log.info("Добавлено в Set {} уникальных элементов", count);
        redisCommands.smembers("setKey").forEach(member -> log.info("Элемент Set: {}", member));
    }

    // Работа с типом ZSet
    public void workWithZSet() {
        log.info("--- Работа с типом ZSet ---");
        Long count = redisCommands.zadd("zsetKey", 1.0, "one", 2.0, "two", 0.5, "zero");
        log.info("Добавлено в ZSet {} элементов", count);
        redisCommands.zrange("zsetKey", 0, -1).forEach(val -> log.info("Элемент ZSet (по порядку): {}", val));
    }

    // Использование команды INCR
    public void useIncrCommand() {
        log.info("--- Использование команды INCR ---");
        redisCommands.set("counter", "10");
        Long val = redisCommands.incr("counter");
        log.info("Значение счетчика после INCR: {}", val);
    }

    // Установка ключа с TTL
    public void setKeyWithTTL() {
        log.info("--- Установка ключа с TTL ---");
        redisCommands.setex("ttlKey", 60, "temporary value");
        log.info("Установлен ключ ttlKey с TTL 60 секунд");
        Long ttl = redisCommands.ttl("ttlKey");
        log.info("Текущий TTL для ttlKey: {} секунд", ttl);
    }
}
