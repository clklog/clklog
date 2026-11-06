package com.zcunsoft.services;

import com.clickhouse.jdbc.ClickHouseDataSource;
import com.zcunsoft.cfg.KafkaSetting;
import com.zcunsoft.cfg.ReceiverSetting;
import com.zcunsoft.cfg.RedisConstsConfig;
import com.zcunsoft.clklog.traffic.SearchEngineIpSnapshot;
import com.zcunsoft.handlers.ConstsDataHolder;
import com.zcunsoft.model.LogBean;
import com.zcunsoft.model.RawMessage;
import com.zcunsoft.util.ObjectMapperUtil;
import nl.basjes.parse.useragent.AbstractUserAgentAnalyzer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/** 在指定的本地隔离 ClickHouse 库中验证接收端混合批次的互斥分表。 */
@EnabledIfSystemProperty(named = "traffic.test.clickhouse", matches = ".+")
class TrafficClickHouseIntegrationTest {
    @Test
    void writesNormalAndBotEventsToSeparateTables() throws Exception {
        String url = System.getProperty("traffic.test.clickhouse");
        if (!url.matches("jdbc:clickhouse://127\\.0\\.0\\.1:[0-9]+/traffic_test")) {
            throw new IllegalArgumentException("Integration test requires the isolated local traffic_test database");
        }
        JdbcTemplate jdbc = new JdbcTemplate(new ClickHouseDataSource(
                url, new Properties()));
        ReceiverSetting config = new ReceiverSetting();
        config.setEnableSimpleVersion(true);
        SearchEngineIpLibraryService library = mock(SearchEngineIpLibraryService.class);
        when(library.snapshot()).thenReturn(SearchEngineIpSnapshot.empty());
        ReceiveServiceImpl service = spy(new ReceiveServiceImpl(new ConstsDataHolder(), new ObjectMapperUtil(),
                mock(StringRedisTemplate.class), mock(AbstractUserAgentAnalyzer.class), jdbc, config,
                mock(KafkaSetting.class), new RedisConstsConfig(), library));
        service.validateBotTable();
        String id = UUID.randomUUID().toString();
        LogBean normal = event("normal-" + id);
        LogBean bot = event("bot-" + id);
        bot.setUserAgent("Googlebot/2.1");
        bot.setDeviceClass("Robot");
        bot.setAgentClass("Robot");
        RawMessage message = new RawMessage();
        doReturn(Arrays.asList(normal, bot)).when(service).analysisData(message);
        service.saveToClickHouse(Collections.singletonList(message));
        assertEquals(1L, jdbc.queryForObject("SELECT count() FROM log_analysis WHERE distinct_id='normal-" + id + "'", Long.class));
        assertEquals(1L, jdbc.queryForObject("SELECT count() FROM log_analysis_bot WHERE distinct_id='bot-" + id + "' "
                + "AND has(bot_reason, 'search_engine_ua') AND search_engine_verify_status='unverified' AND agent_class='Robot'", Long.class));
        assertEquals(0L, jdbc.queryForObject("SELECT count() FROM log_analysis WHERE distinct_id='bot-" + id + "'", Long.class));
    }

    private static LogBean event(String id) {
        LogBean event = new LogBean();
        event.setDistinctId(id);
        event.setLogTime(Timestamp.valueOf("2024-01-01 00:00:00"));
        event.setStatDate("2024-01-01");
        event.setCreateTime("2024-01-01 00:00:00");
        return event;
    }
}
