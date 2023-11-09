# 项目简介

ClkLog是一款记录用户行为分析和画像的免费可商用开源软件，技术人员可快速搭建私有的应用系统。项目基于神策分析SDK，采用ClickHouse数据库对采集数据进行存储，采用前后端分离的方式来实现的访问统计和用户画像分析系统。在这里，你可以轻松看到用户访问网页、APP、小程序或业务系统的行为轨迹，同时也可以从时间、地域、渠道、用户访客类型等多维度了解用户的全方位信息。

# 核心功能

- **数据采集**：支持网页、小程序、IOS、Android等多端数据采集

- **流量概览**：提供流量渠道、设备、地域、访客类型多维度分析

- **用户画像**：解析用户唯一ID，定位追踪用户全生命周期画像

- **数据下载**：支持各项汇总数据、明细数据的下载

# 技术栈选择

- **后端**：Redis 、Zookeeper、Kafka 、Flink

- **前端**：vue、vue-element-admin、element-ui 、echarts

- **数据**：Clickhouse

# 示意图

| ![](docs/assets/imgs/1.png) | ![](docs/assets/imgs/2.png) |
| --------------------------- | --------------------------- |
| ![](docs/assets/imgs/3.png) | ![](docs/assets/imgs/4.png) |
| ![](docs/assets/imgs/5.png) | ![](docs/assets/imgs/6.png) |
| ![](docs/assets/imgs/7.png) | ![](docs/assets/imgs/8.png) |

# 在线体验

演示地址：<a href="https://demo.clklog.com" target="_blank">https://demo.clklog.com</a>

# 快速接入

官方文档：<a href="https://clklog.com">https://clklog.com</a>

<!-- # 开源社区

问题反馈：[https://github.com/clklog/clklog/issues](https://github.com/clklog/clklog/issues)

参与讨论：[https://github.com/orgs/clklog/discussions](https://github.com/orgs/clklog/discussions) -->

# 协议许可

- 开源协议：[AGPL V3.0](https://www.gnu.org/licenses/agpl-3.0.en.html)

- 免费使用：Clklog遵循AGPL V3.0开源许可证, 使用的组织或个人在复制、分发、转发或修改时请遵守相关条款，不得移除ClkLog相关版权标识进行。如有违反，ClkLog将保留对侵权者追究责任的权利。

- 商业使用：请联系邮箱`info@clklog.com`进行细节咨询
