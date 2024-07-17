
## 什么是AccessLog

AccessLog 是ClkLog下的一款针对服务器日志的采集和数据分析的开源软件，技术人员可快速完成私有化部署。

AccessLog 的核心是系统日志的采集与清洗、系统日志数据的分析与展示。

AccessLog 主要是通过采集工具（推荐：vector或filebeat）对服务器（如nginx、iis）日志进行采集，通过相关服务对采集数据进行处理，然后在可视化的日志查询分析界面对服务器日志进行查询分析。

## 核心功能

- **数据采集**: 对服务端的access日志进行采集

- **数据处理**: 对采集日志数据进行处理

- **可视化查询分析**: 通过可视化查询页面对日志进行多维度数据分析

## 技术栈选择

- **后端**：Redis 、Kafka 、Flink

- **前端**：vue、vue-element-admin、element-ui 、echarts

- **数据**：Clickhouse

## 系统架构

<!-- tabs:start -->

#### **标准模式**

![](../assets/imgs/accesslog/all-process.png)

#### **快速模式**

![](../assets/imgs/accesslog/fast-process.png)

<!-- tabs:end -->

## 项目组成

- **处理服务  【accesslog-processing-sample】**： 直接将数据写入clickhouse。

- **处理服务  【accesslog-processing】**： 依托flink，消费kafka数据并存入clickhouse。

- **统计接口 【accesslog-api】**： 提供多维度数据统计接口。

- **统计展示 【accesslog-ui】**：基于 vue-element-admin 实现的统计分析数据界面展示。

## 示意图

| ![](../assets/imgs/accesslog/1.png) | ![](../assets/imgs/accesslog/2.png) |
| ----------------------------------- | ----------------------------------- |
| ![](../assets/imgs/accesslog/3.png) | ![](../assets/imgs/accesslog/4.png) |
| ![](../assets/imgs/accesslog/5.png) | ![](../assets/imgs/accesslog/6.png) |
| ![](../assets/imgs/accesslog/7.png) | ![](../assets/imgs/accesslog/8.png) |
