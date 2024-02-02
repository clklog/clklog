package com.zcunsoft.cfg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * The Class LogCollectorSetting.
 *
 *
 */
@Configuration
public class ReceiverSetting {


    @Value(value = "${spring.kafka.producer.topic:clklog}")
    private String topicName = "clklog";

    @Value(value = "${receiver.thread-count:2}")
    private int threadCount = 2;

    @Value(value = "${receiver.app-list:hqq}")
    private List<String> appList;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public List<String> getAppList() {
        return appList;
    }

    public void setAppList(List<String> appList) {
        this.appList = appList;
    }
}
