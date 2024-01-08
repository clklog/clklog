package com.zcunsoft.clklog.analysis.entry;


import com.zcunsoft.clklog.analysis.bean.LogBean;
import com.zcunsoft.clklog.analysis.function.LogRichMapper;
import com.zcunsoft.clklog.analysis.sink.LogAnalysisClickHouseSink;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.File;
import java.util.List;

public class JieXiJson {

public static void main(String[] args) throws Exception {
    ParameterTool parameters = ParameterTool.fromPropertiesFile(System.getProperty("user.dir") + File.separator + "config.properties");

    String kafkaBootstrapServers = parameters.get("kafka.bootstrap.server", "localhost:9092");
    String kafkaConsumeTopic = parameters.get("kafka.topic", "clklog");
    String kafkaConsumeGroup = parameters.get("kafka.group-id", "clklog-group");
    String flinkCheckPoint = parameters.get("flink.checkpoint", "file:///usr/local/services/clklogprocessing/checkpoints");
    int flinkParallelism = parameters.getInt("flink.parallelism", 1);
    String flinkDataSourceName = parameters.get("flink.data-source-name", "Kafka Source");

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
    env.setStateBackend(new FsStateBackend(flinkCheckPoint));

    KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
      .setBootstrapServers(kafkaBootstrapServers)
      .setTopics(kafkaConsumeTopic)
      .setGroupId(kafkaConsumeGroup)
      .setStartingOffsets(OffsetsInitializer.earliest())
      .setValueOnlyDeserializer(new SimpleStringSchema())
      .build();
    DataStreamSource<String> streamSource = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), flinkDataSourceName);

    SingleOutputStreamOperator<List<LogBean>> value = streamSource.map(new LogRichMapper());

    LogAnalysisClickHouseSink clickhouseSink = new LogAnalysisClickHouseSink();
    value.addSink(clickhouseSink);

    env.execute("clklogdeal");
  }
}

