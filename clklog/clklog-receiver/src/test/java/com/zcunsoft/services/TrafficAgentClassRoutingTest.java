package com.zcunsoft.services;

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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 客户端类型单独命中时，验证机器人分表及类型字段绑定。 */
class TrafficAgentClassRoutingTest {
    @Test
    void routesAgentClassMatchAndBindsDeviceClass() throws Exception {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        Connection connection = mock(Connection.class);
        when(statement.getConnection()).thenReturn(connection);
        when(connection.createArrayOf(any(String.class), any(Object[].class))).thenReturn(mock(Array.class));
        when(jdbc.batchUpdate(contains("insert into log_analysis_bot ("), any(BatchPreparedStatementSetter.class)))
                .thenAnswer(invocation -> {
                    String sql = invocation.getArgument(0);
                    assertTrue(sql.contains("ai_verify_status,agent_class,bot_category,bot_subcategory)"));
                    assertEquals(111, sql.chars().filter(c -> c == '?').count());
                    BatchPreparedStatementSetter setter = invocation.getArgument(1);
                    setter.setValues(statement, 0);
                    return new int[]{1};
                });
        SearchEngineIpLibraryService library = mock(SearchEngineIpLibraryService.class);
        when(library.snapshot()).thenReturn(SearchEngineIpSnapshot.empty());
        ReceiveServiceImpl service = spy(new ReceiveServiceImpl(new ConstsDataHolder(), new ObjectMapperUtil(),
                mock(StringRedisTemplate.class), mock(AbstractUserAgentAnalyzer.class), jdbc, new ReceiverSetting(),
                mock(KafkaSetting.class), new RedisConstsConfig(), library));
        LogBean event = new LogBean();
        event.setUserAgent("example");
        event.setDeviceClass("Mobile");
        event.setAgentClass("Robot Mobile");
        event.setStatDate("2024-01-01");
        RawMessage message = new RawMessage();
        doReturn(Collections.singletonList(event)).when(service).analysisData(message);

        service.saveToClickHouse(Collections.singletonList(message));

        verify(jdbc).batchUpdate(contains("insert into log_analysis_bot ("), any(BatchPreparedStatementSetter.class));
        verify(jdbc, never()).batchUpdate(contains("insert into log_analysis ("), any(BatchPreparedStatementSetter.class));
        verify(statement).setString(105, "Mobile");
        verify(statement).setString(109, "Robot Mobile");
        verify(statement).setString(110, "other");
        verify(statement).setString(111, "robot_unknown");
    }
}
