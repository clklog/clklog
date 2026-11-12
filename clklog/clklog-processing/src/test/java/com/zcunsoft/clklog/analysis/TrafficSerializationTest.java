package com.zcunsoft.clklog.analysis;

import com.zcunsoft.clklog.analysis.bean.LogBean;
import com.zcunsoft.clklog.analysis.bean.LogBeanCollection;
import com.zcunsoft.clklog.traffic.SearchEngineIpSnapshot;
import com.zcunsoft.clklog.traffic.UaRuleSet;
import com.zcunsoft.clklog.traffic.UaTrafficClassifier;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.core.memory.DataInputDeserializer;
import org.apache.flink.core.memory.DataOutputSerializer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 验证 Flink 序列化单条事件及事件集合后，机器人判定结果仍然保留。 */
class TrafficSerializationTest {
    @Test
    void preservesDecisionAcrossFlinkSerialization() throws Exception {
        LogBean event = new LogBean();
        event.setDistinctId("bot");
        event.setAgentClass("Robot");
        event.setDeviceClass("Robot");
        event.setTrafficDecision(new UaTrafficClassifier().classify("Googlebot/2.1", "192.0.2.1",
                "Robot", "Robot", "Googlebot", UaRuleSet.compile("", false), SearchEngineIpSnapshot.empty()));
        TypeSerializer<LogBean> serializer = TypeInformation.of(LogBean.class).createSerializer(new ExecutionConfig());
        DataOutputSerializer output = new DataOutputSerializer(512);
        serializer.serialize(event, output);
        LogBean decoded = serializer.deserialize(new DataInputDeserializer(output.getCopyOfBuffer()));
        assertEquals("Robot", decoded.getAgentClass());
        assertEquals("Robot", decoded.getTrafficDecision().getDeviceClass());
        assertTrue(decoded.getTrafficDecision().isBot());
        assertEquals("search_engine", decoded.getTrafficDecision().getBotCategory());
        assertEquals("google", decoded.getTrafficDecision().getBotSubcategory());
        assertEquals("unverified", decoded.getTrafficDecision().getVerificationStatus());

        LogBeanCollection collection = new LogBeanCollection();
        collection.setData(new ArrayList<>(Collections.singletonList(event)));
        TypeSerializer<LogBeanCollection> collectionSerializer =
                TypeInformation.of(LogBeanCollection.class).createSerializer(new ExecutionConfig());
        DataOutputSerializer collectionOutput = new DataOutputSerializer(512);
        collectionSerializer.serialize(collection, collectionOutput);
        LogBeanCollection decodedCollection = collectionSerializer.deserialize(
                new DataInputDeserializer(collectionOutput.getCopyOfBuffer()));
        assertEquals("google", decodedCollection.getData().get(0).getTrafficDecision().getBotSubcategory());
    }
}
