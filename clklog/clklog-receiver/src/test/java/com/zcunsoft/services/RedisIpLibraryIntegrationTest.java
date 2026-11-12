package com.zcunsoft.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 连接指定的本地 Redis，验证 IP 库记录加载与删除后的快照更新。 */
@EnabledIfSystemProperty(named = "traffic.test.redis.port", matches = "[0-9]+")
class RedisIpLibraryIntegrationTest {
    @Test
    void loadsAndRemovesPublishedRecords() {
        int port = Integer.parseInt(System.getProperty("traffic.test.redis.port"));
        LettuceConnectionFactory factory = new LettuceConnectionFactory("127.0.0.1", port);
        factory.afterPropertiesSet();
        StringRedisTemplate redis = new StringRedisTemplate(factory);
        redis.afterPropertiesSet();
        String key = "traffic-test:" + UUID.randomUUID();
        long now = System.currentTimeMillis();
        try {
            String json = "{\"engine\":\"google\",\"cidr\":\"192.0.2.0/24\",\"source\":\"official_feed\","
                    + "\"verified_at\":" + (now - 1000) + ",\"expires_at\":" + (now + 60000) + "}";
            redis.opsForHash().put(key, "google|192.0.2.0/24", json);
            SearchEngineIpLibraryService service = new SearchEngineIpLibraryService(redis, new ObjectMapper(), key);
            service.refresh();
            assertEquals("verified", service.snapshot().match("google", "192.0.2.1", now).getStatus());
            redis.delete(key);
            service.refresh();
            assertEquals("unverified", service.snapshot().match("google", "192.0.2.1", now).getStatus());
        } finally {
            redis.delete(key);
            factory.destroy();
        }
    }
}
