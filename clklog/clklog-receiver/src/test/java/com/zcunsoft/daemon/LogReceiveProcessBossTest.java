package com.zcunsoft.daemon;

import com.zcunsoft.cfg.ReceiverSetting;
import com.zcunsoft.handlers.ConstsDataHolder;
import com.zcunsoft.model.RawMessage;
import com.zcunsoft.services.IReceiveService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/** 验证失败批次不会无限重试，后续批次仍会继续处理。 */
class LogReceiveProcessBossTest {
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void continuesAfterFailedBatch(boolean simpleVersion) throws Exception {
        ConstsDataHolder holder = new ConstsDataHolder();
        ReceiverSetting settings = new ReceiverSetting();
        settings.setThreadCount(1);
        settings.setEnableSimpleVersion(simpleVersion);
        IReceiveService service = mock(IReceiveService.class);
        RawMessage invalid = new RawMessage();
        RawMessage valid = new RawMessage();
        valid.setToken("valid");
        CountDownLatch failed = new CountDownLatch(1);
        CountDownLatch processed = new CountDownLatch(1);
        org.mockito.stubbing.Answer<Void> answer = invocation -> {
            List<RawMessage> messages = invocation.getArgument(0);
            if (messages.contains(invalid)) {
                failed.countDown();
                throw new DataIntegrityViolationException("project_token cannot be null");
            }
            processed.countDown();
            return null;
        };
        if (simpleVersion) {
            doAnswer(answer).when(service).saveToClickHouse(anyList());
        } else {
            doAnswer(answer).when(service).enqueueKafka(anyList());
        }
        LogReceiveProcessBoss boss = new LogReceiveProcessBoss();
        setField(boss, "constsDataHolder", holder);
        setField(boss, "serverSettings", settings);
        setField(boss, "ireceiveService", service);
        boss.start();
        try {
            holder.getLogQueue().put(invalid);
            assertTrue(failed.await(3, TimeUnit.SECONDS), "异常批次未开始处理");
            holder.getLogQueue().put(valid);
            assertTrue(processed.await(10, TimeUnit.SECONDS), "后续批次未继续处理");
            if (simpleVersion) {
                verify(service, times(1)).saveToClickHouse(argThat(batch -> batch.contains(invalid)));
                verify(service, times(1)).saveToClickHouse(argThat(batch -> batch.contains(valid)));
            } else {
                verify(service, times(1)).enqueueKafka(argThat(batch -> batch.contains(invalid)));
                verify(service, times(1)).enqueueKafka(argThat(batch -> batch.contains(valid)));
            }
        } finally {
            boss.stop();
        }
    }

    private static void setField(LogReceiveProcessBoss boss, String name, Object value) throws Exception {
        Field field = LogReceiveProcessBoss.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(boss, value);
    }
}
