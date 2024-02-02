package com.zcunsoft.services;

import com.zcunsoft.cfg.ReceiverSetting;
import com.zcunsoft.dto.QueryCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;

@Service
public class ReceiveServiceImpl implements IReceiveService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ReceiverSetting serverSettings;

    public ReceiveServiceImpl(KafkaTemplate<String, String> kafkaTemplate, ReceiverSetting serverSettings) {
        this.kafkaTemplate = kafkaTemplate;
        this.serverSettings = serverSettings;
    }

    @Override
    public void enqueueKafka(List<QueryCriteria> queryCriteriaList) {

        for (QueryCriteria queryCriteria : queryCriteriaList) {
            String dataFinal = queryCriteria.getData();
            if (dataFinal != null && !dataFinal.trim().isEmpty()) {
                String logData = String.valueOf(System.currentTimeMillis()) + ',' + queryCriteria.getProject() + ',' + queryCriteria.getToken() + ',' + queryCriteria.getCrc() + ',' + queryCriteria.getGzip() + ',' + queryCriteria.getClientIp() + ',' + dataFinal;

                ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(serverSettings.getTopicName(),
                        logData);
                future.addCallback(o -> {
                }, err -> logger.error("发送失败,", err));
            }
        }
    }
}
