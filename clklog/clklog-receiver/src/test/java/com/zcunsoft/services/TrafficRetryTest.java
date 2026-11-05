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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 验证数据库批次失败时立即抛出，不重试或转写其他目标表。 */
class TrafficRetryTest {
    @Test
    void propagatesFailedBotBatchWithoutRetryOrRoutingToNormalTable() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.batchUpdate(contains("log_analysis_bot"), any(BatchPreparedStatementSetter.class)))
                .thenThrow(new IllegalStateException("temporary failure")).thenReturn(new int[]{1});
        SearchEngineIpLibraryService library = mock(SearchEngineIpLibraryService.class);
        when(library.snapshot()).thenReturn(SearchEngineIpSnapshot.empty());
        ReceiveServiceImpl service = spy(new ReceiveServiceImpl(new ConstsDataHolder(), new ObjectMapperUtil(),
                mock(StringRedisTemplate.class), mock(AbstractUserAgentAnalyzer.class), jdbc, new ReceiverSetting(),
                mock(KafkaSetting.class), new RedisConstsConfig(), library));
        LogBean bot = new LogBean();
        bot.setUserAgent("Googlebot/2.1");
        RawMessage message = new RawMessage();
        doReturn(Collections.singletonList(bot)).when(service).analysisData(message);
        assertThrows(IllegalStateException.class, () -> service.saveToClickHouse(Collections.singletonList(message)));
        verify(jdbc, times(1)).batchUpdate(contains("log_analysis_bot"), any(BatchPreparedStatementSetter.class));
        verify(jdbc, times(0)).batchUpdate(contains("insert into log_analysis ("), any(BatchPreparedStatementSetter.class));
    }
}
