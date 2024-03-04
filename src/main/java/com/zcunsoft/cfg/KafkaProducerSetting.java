package com.zcunsoft.cfg;

import lombok.Data;

@Data
public class KafkaProducerSetting {
    private int retries;

    private String topic;

    private String acks;

    private String keySerializer;

    private String valueSerializer;

    private String clientId;

}
