package com.zcunsoft.clklog.analysis.sink;

import com.zcunsoft.clklog.analysis.bean.LogBean;
import com.zcunsoft.clklog.analysis.bean.LogBeanCollection;
import com.zcunsoft.clklog.traffic.SearchEngineIpSnapshot;
import com.zcunsoft.clklog.traffic.UaRuleSet;
import com.zcunsoft.clklog.traffic.UaTrafficClassifier;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrafficSinkRoutingTest {
    @Test
    void writesDeviceClassForAgentClassRobot() throws Exception {
        AtomicReference<String> deviceClass = new AtomicReference<>();
        AtomicReference<String> agentClass = new AtomicReference<>();
        AtomicReference<String> botCategory = new AtomicReference<>();
        AtomicReference<String> botSubcategory = new AtomicReference<>();
        AtomicInteger botBatches = new AtomicInteger();
        AtomicInteger normalBatches = new AtomicInteger();
        PreparedStatement botStatement = statement(deviceClass, agentClass, botCategory, botSubcategory, botBatches);
        PreparedStatement normalStatement = statement(new AtomicReference<>(), new AtomicReference<>(),
                new AtomicReference<>(), new AtomicReference<>(), normalBatches);
        Connection connection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class}, (proxy, method, args) -> null);

        LogAnalysisClickHouseSink sink = new LogAnalysisClickHouseSink();
        assertTrue(sink.botSql().contains("ai_verify_status,agent_class,bot_category,bot_subcategory)"));
        assertEquals(111, sink.botSql().chars().filter(c -> c == '?').count());
        sink.conn = connection;
        sink.botStatement = botStatement;
        sink.normalStatement = normalStatement;
        LogBean bot = event();
        bot.setAgentClass("Robot Mobile");
        bot.setTrafficDecision(new UaTrafficClassifier().classify("example", "192.0.2.1",
                "Mobile", "Robot Mobile", "", UaRuleSet.compile("", false),
                SearchEngineIpSnapshot.empty()));
        LogBeanCollection collection = new LogBeanCollection();
        collection.setData(Arrays.asList(event(), bot));
        sink.invoke(Collections.singletonList(collection), null);

        assertEquals("Mobile", deviceClass.get());
        assertEquals("Robot Mobile", agentClass.get());
        assertEquals("other", botCategory.get());
        assertEquals("robot_unknown", botSubcategory.get());
        assertEquals(1, botBatches.get());
        assertEquals(1, normalBatches.get());
    }

    private static PreparedStatement statement(AtomicReference<String> deviceClass,
                                               AtomicReference<String> agentClass, AtomicReference<String> botCategory,
                                               AtomicReference<String> botSubcategory, AtomicInteger batches) {
        return (PreparedStatement) Proxy.newProxyInstance(PreparedStatement.class.getClassLoader(),
                new Class<?>[]{PreparedStatement.class}, (proxy, method, args) -> {
                    if ("setString".equals(method.getName()) && Integer.valueOf(105).equals(args[0])) {
                        deviceClass.set((String) args[1]);
                    } else if ("setString".equals(method.getName()) && Integer.valueOf(109).equals(args[0])) {
                        agentClass.set((String) args[1]);
                    } else if ("setString".equals(method.getName()) && Integer.valueOf(110).equals(args[0])) {
                        botCategory.set((String) args[1]);
                    } else if ("setString".equals(method.getName()) && Integer.valueOf(111).equals(args[0])) {
                        botSubcategory.set((String) args[1]);
                    } else if ("executeBatch".equals(method.getName())) {
                        batches.incrementAndGet();
                        return new int[0];
                    }
                    return null;
                });
    }

    private static LogBean event() {
        LogBean event = new LogBean();
        event.setLogTime(Timestamp.valueOf("2024-01-01 00:00:00"));
        event.setStatDate("2024-01-01");
        return event;
    }
}
