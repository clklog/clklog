
# 项目介绍

## 什么是ClkLog

ClkLog是一款记录用户行为分析和画像的免费可商用开源软件，技术人员可快速搭建私有的应用系统。项目基于神策分析SDK，采用ClickHouse数据库对采集数据进行存储，采用前后端分离的方式来实现的访问统计和用户画像分析系统。在这里，你可以轻松看到用户访问网页、APP、小程序或业务系统的行为轨迹，同时也可以从时间、地域、渠道、用户访客类型等多维度了解用户的全方位信息。

## 核心功能

- **数据采集**：支持网页、小程序、IOS、Android等多端数据采集

- **数据治理**：制定清洗规制、实现数据自动化清洗和治理

- **流量概览**：提供流量渠道、设备、地域、访客类型多维度分析

- **用户画像**：解析用户唯一ID，定位追踪用户全生命周期画像

- **数据下载**：支持各项汇总数据、明细数据的下载

## 技术栈选择

- **后端**：Redis 、Zookeeper、Kafka 、Flink

- **前端**：vue、vue-element-admin、element-ui 、echarts

- **数据**：Clickhouse

## 项目组成

- **接收服务 clklog-receiver】**：接收客服端采集的日志，存入kafka。

  <a href="https://github.com/clklog/clklog-receiver" target="_blank">[GitHub 下载]</a>

- **处理脚本 【clickhouse-script】**：基于Clickhouse集群中的log_analysis表进行多维计算，获得各个维度上的统计报表。使用Cron来进行任务的定时调度。

  <a href="https://github.com/clklog/clklog-scripts" target="_blank">[GitHub 下载]</a>

- **处理服务  【clklog-processing】**： 依托flink，消费kafka数据，存入clickhouse。

  <a href="https://github.com/clklog/clklog-processing" target="_blank">[GitHub 下载]</a>

- **统计接口 【clklog-api】**： 提供多维度数据统计接口。

  <a href="https://github.com/clklog/clklog-api" target="_blank">[GitHub 下载]</a>

- **统计展示 【clklog-ui】**：基于 vue-element-admin 实现的统计分析数据界面展示。

 <a href="https://github.com/clklog/clklog-ui" target="_blank">[GitHub 下载]</a>

## 示意图

| ![](assets/imgs/1.png) | ![](assets/imgs/2.png) |
| ---------------------- | ---------------------- |
| ![](assets/imgs/3.png) | ![](assets/imgs/4.png) |
| ![](assets/imgs/5.png) | ![](assets/imgs/6.png) |
| ![](assets/imgs/7.png) | ![](assets/imgs/8.png) |
