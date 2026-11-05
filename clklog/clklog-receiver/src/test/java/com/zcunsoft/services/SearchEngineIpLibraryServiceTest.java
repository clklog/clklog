package com.zcunsoft.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** 验证 IP 库刷新成功、Redis 读取失败及读取到空库时的快照行为。 */
class SearchEngineIpLibraryServiceTest {
    @Test
    @SuppressWarnings("unchecked")
    void replacesSnapshotOnlyAfterSuccessfulRefresh() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        HashOperations<String, Object, Object> hash = mock(HashOperations.class);
        when(redis.opsForHash()).thenReturn(hash);
        long now = System.currentTimeMillis();
        String json = "{\"engine\":\"google\",\"cidr\":\"192.0.2.0/24\",\"source\":\"official_feed\","
                + "\"verified_at\":" + (now - 1000) + ",\"expires_at\":" + (now + 60000)
                + ",\"library_version\":\"v1\"}";
        Map<Object, Object> records = Collections.<Object, Object>singletonMap("google|192.0.2.0/24", json);
        when(hash.entries("test")).thenReturn(records).thenThrow(new RuntimeException("offline"))
                .thenReturn(Collections.emptyMap());
        SearchEngineIpLibraryService service = new SearchEngineIpLibraryService(redis, new ObjectMapper(), "test");
        service.refresh();
        assertEquals("verified", service.snapshot().match("google", "192.0.2.1", now).getStatus());
        service.refresh();
        assertEquals("verified", service.snapshot().match("google", "192.0.2.1", now).getStatus());
        service.refresh();
        assertEquals("unverified", service.snapshot().match("google", "192.0.2.1", now).getStatus());
    }
}
