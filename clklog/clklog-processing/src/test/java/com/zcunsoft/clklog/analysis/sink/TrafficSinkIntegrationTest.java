package com.zcunsoft.clklog.analysis.sink;

import com.clickhouse.jdbc.ClickHouseDataSource;
import com.zcunsoft.clklog.analysis.bean.LogBean;
import com.zcunsoft.clklog.analysis.bean.LogBeanCollection;
import com.zcunsoft.clklog.traffic.SearchEngineIpSnapshot;
import com.zcunsoft.clklog.traffic.UaRuleSet;
import com.zcunsoft.clklog.traffic.UaTrafficClassifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 在指定的本地隔离 ClickHouse 库中验证 Flink Sink 的混合批次分表。 */
@EnabledIfSystemProperty(named = "traffic.test.clickhouse", matches = ".+")
class TrafficSinkIntegrationTest {
    @Test
    void sendsMixedBatchToMatchingTables() throws Exception {
        String url = System.getProperty("traffic.test.clickhouse");
        if (!url.matches("jdbc:clickhouse://127\\.0\\.0\\.1:[0-9]+/traffic_test")) {
            throw new IllegalArgumentException("Integration test requires the isolated local traffic_test database");
        }
        try (Connection conn = new ClickHouseDataSource(url, new Properties()).getConnection()) {
            LogAnalysisClickHouseSink sink = new LogAnalysisClickHouseSink();
            sink.conn = conn;
            sink.normalStatement = conn.prepareStatement(sink.sql);
            sink.botStatement = conn.prepareStatement(sink.botSql());
            String id = UUID.randomUUID().toString();
            LogBean normal = event("flink-normal-" + id);
            LogBean bot = event("flink-bot-" + id);
            bot.setAgentClass("Robot");
            bot.setTrafficDecision(new UaTrafficClassifier().classify("Googlebot/2.1", "192.0.2.1",
                    "Robot", "Googlebot", "", UaRuleSet.compile("", false), SearchEngineIpSnapshot.empty()));
            LogBeanCollection collection = new LogBeanCollection();
            collection.setData(Arrays.asList(normal, bot));
            sink.invoke(Collections.singletonList(collection), null);
            try (Statement statement = conn.createStatement()) {
                try (ResultSet result = statement.executeQuery(
                        "SELECT count() FROM log_analysis WHERE distinct_id='flink-normal-" + id + "'")) {
                    result.next();
                    assertEquals(1, result.getInt(1));
                }
                try (ResultSet result = statement.executeQuery(
                        "SELECT count() FROM log_analysis_bot WHERE distinct_id='flink-bot-" + id + "' AND has(bot_reason, 'search_engine_ua') AND agent_class='Robot'")) {
                    result.next();
                    assertEquals(1, result.getInt(1));
                }
            }
            sink.normalStatement.close();
            sink.botStatement.close();
        }
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
