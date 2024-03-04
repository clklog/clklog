package com.zcunsoft.cfg;

import lombok.Data;

@Data
public class KafkaSetting {

    private String bootstrapServers;

    private KafkaProducerSetting producer;

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public KafkaProducerSetting getProducer() {
        return producer;
    }

    public void setProducer(KafkaProducerSetting producer) {
        this.producer = producer;
    }
}

