package com.zcunsoft.util;

import com.zcunsoft.cfg.KafkaSetting;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaProducerUtil {
    private final Logger logger = LogManager.getLogger(this.getClass());


    public static KafkaProducer<String, String> producer;
    static KafkaSetting _kafkaSetting;

    KafkaProducerUtil() {
        Properties props = new Properties();
        props.put("bootstrap.servers", _kafkaSetting.getBootstrapServers());
        props.put("retries", _kafkaSetting.getProducer().getRetries());
        props.put("acks", _kafkaSetting.getProducer().getAcks());
        props.put("client.id", _kafkaSetting.getProducer().getClientId());
        props.put("key.serializer", _kafkaSetting.getProducer().getKeySerializer());
        props.put("value.serializer", _kafkaSetting.getProducer().getValueSerializer());
        producer = new KafkaProducer<String, String>(props);
    }

    private static class ProducerKafkaHolder {
        private static final KafkaProducerUtil instance = new KafkaProducerUtil();
    }

    public static KafkaProducerUtil getInstance(KafkaSetting kafkaSetting) {
        _kafkaSetting = kafkaSetting;
        return ProducerKafkaHolder.instance;
    }

    public void sendMessgae(ProducerRecord message) throws Exception {

        Future<RecordMetadata> future = producer.send(message, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception != null) {
                    logger.error("producer send error", exception);
                }
            }
        });
    }

    public void sendMessgae(String topic, String value) throws Exception {
        sendMessgae(new ProducerRecord<String, String>(topic, value));
    }

    public void sendMessgae(String topic, String key, String value) throws Exception {
        sendMessgae(new ProducerRecord(topic, key, value));
    }

    public void flush() {
        producer.flush();
    }

    public void close() {
        producer.close();
    }
}
