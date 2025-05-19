package com.zcunsoft.clklog.analysis.entry;


import com.zcunsoft.clklog.analysis.bean.LogBeanCollection;
import com.zcunsoft.clklog.analysis.function.LogRichMapper;
import com.zcunsoft.clklog.analysis.sink.LogAnalysisClickHouseSink;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.shaded.guava30.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JieXiJson {

    public static void main(String[] args) throws Exception {
        ParameterTool parameters = ParameterTool.fromPropertiesFile(System.getProperty("user.dir") + File.separator + "config.properties");

        String kafkaBootstrapServers = parameters.get("kafka.bootstrap.server", "localhost:9092");
        String flinkCheckPoint = parameters.get("flink.checkpoint", "file:///usr/local/services/clklogprocessing/checkpoints");
        int flinkParallelism = parameters.getInt("flink.parallelism", 1);
        String flinkDataSourceName = parameters.get("flink.clklog-data-source-name", "ClklogKafkaSource");


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(flinkParallelism);
        env.getConfig().setGlobalJobParameters(parameters);
        //checkpoint配置
        env.enableCheckpointing(5000);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        // 将检查点的元数据信息定期写入外部系统，如果job失败时，检查点不会被清除
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //checkpoint路径
        env.setStateBackend(new HashMapStateBackend());
        env.getCheckpointConfig().setCheckpointStorage(flinkCheckPoint);

        // handle clklog
        String clklogKafkaConsumeTopic = parameters.get("kafka.clklog-topic", "clklog");
        String clklogKafkaConsumeGroup = parameters.get("kafka.clklog-group-id", "clklog-group");

        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(kafkaBootstrapServers)
                .setTopics(clklogKafkaConsumeTopic)
                .setGroupId(clklogKafkaConsumeGroup)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        DataStreamSource<String> streamSource = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), flinkDataSourceName);
        SingleOutputStreamOperator<LogBeanCollection> value = streamSource.map(new LogRichMapper());

        int timeWindow = parameters.getInt("flink.time-window", 10);
        SingleOutputStreamOperator<List<LogBeanCollection>> valueInWindow = value.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(timeWindow))).apply(new AllWindowFunction<LogBeanCollection, List<LogBeanCollection>, TimeWindow>() {
            @Override
            public void apply(TimeWindow window, Iterable<LogBeanCollection> values, Collector<List<LogBeanCollection>> out) {
                ArrayList<LogBeanCollection> logBeanCollectionList = Lists.newArrayList(values);
                if (!logBeanCollectionList.isEmpty()) {
                    out.collect(logBeanCollectionList);
                }
            }
        }).name("clklog-window-all-to-ck");

        LogAnalysisClickHouseSink clickhouseSink = new LogAnalysisClickHouseSink();
        valueInWindow.addSink(clickhouseSink);

        env.execute(parameters.get("flink.clklog-job-name", "clklog-processing"));
    }
}

