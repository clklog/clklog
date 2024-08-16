# Project Introduction/项目简介

ClkLog is an open-source system that records and analyzes user online behaviors to build a user profile. Technical personnel can quickly complete private deployment.<br>
ClkLog是一款记录并分析用户行为和画像的开源软件，技术人员可快速完成私有化部署。<br><br>
ClkLog is based on the Sensors Analysis SDK. It uses the ClickHouse database to store collected data by using the front-end and back-end separation method. Here, you can easily see the users’ behavior track when they access the web pages, mobile apps, Wechat mini-programs or other business systems. You can also collect the users’ all-round information from multiple dimensions such as time, region, channel, visitor type, etc.<br>
ClkLog基于神策分析SDK，采用ClickHouse数据库对采集数据进行存储，使用前后端分离的方式来实现。在这里，你可以轻松看到用户访问网页、APP、小程序或业务系统的行为轨迹，同时也可以从时间、地域、渠道、用户访客类型等多维度了解用户的全方位信息。<br><br>
ClkLog also provides a commercial version with more advanced analysis functions based on the open-source community version.<br>
ClkLog在开源社区版本的基础上同时提供拥有更多高级分析功能的商业版本。<br>

# Core Functions/核心功能

- **Data collection**: supports data collection from multiple channels such as web pages, Wechat mini-programs, IOS, Android, etc.<br>
- **数据采集**：支持网页、小程序、IOS、Android等多端数据采集<br><br>
- **Traffic overview**: provides multi-dimensional analysis from channels, devices, regions to visitor types.<br>
- **流量概览**：提供流量渠道、设备、地域、访客类型多维度分析<br><br>
- **User Profile**: analyzes user unique IDs to locate and track full life cycle user profile.<br>
- **用户画像**：解析用户唯一ID，定位追踪用户全生命周期画像<br><br>
- **Data Summary**: supports downloading of various summarized data and detailed data.<br>
- **数据下载**：支持各项汇总数据、明细数据的下载

# Technology Selection/技术栈选择

- **Backend/后端**：Redis 、Zookeeper、Kafka 、Flink

- **Frontend/前端**：vue、vue-element-admin、element-ui 、echarts

- **Database/数据**：Clickhouse

# Screenshot Samples/示意图

| ![](docs/assets/imgs/1.png) | ![](docs/assets/imgs/2.png) |
| --------------------------- | --------------------------- |
| ![](docs/assets/imgs/3.png) | ![](docs/assets/imgs/4.png) |
| ![](docs/assets/imgs/5.png) | ![](docs/assets/imgs/6.png) |
| ![](docs/assets/imgs/7.png) | ![](docs/assets/imgs/8.png) |

# Online Demo/在线体验

Demo address/演示地址：<a href="https://demo.clklog.com" target="_blank">https://demo.clklog.com</a>

# Quick Start Tutorial/快速接入

Official Documents/官方文档：<a href="https://clklog.com">https://clklog.com</a>

<!-- # 开源社区

问题反馈：[https://github.com/clklog/clklog/issues](https://github.com/clklog/clklog/issues)

参与讨论：[https://github.com/orgs/clklog/discussions](https://github.com/orgs/clklog/discussions) -->

# License Agreement/协议许可

- Open-source agreement/开源协议：[AGPL V3.0](https://www.gnu.org/licenses/agpl-3.0.en.html)
- Free use: Clklog follows the AGPL V3.0 open-source license. Organizations or individuals using it must comply with the relevant terms when copying, distributing, forwarding or modifying it, and must not remove the ClkLog related copyright logo. If violated, ClkLog reserves the right to pursue liability for infringement.<br>
- 免费使用：Clklog遵循AGPL V3.0开源许可证, 使用的组织或个人在复制、分发、转发或修改时请遵守相关条款，不得移除ClkLog相关版权标识进行。如有违反，ClkLog将保留对侵权者追究责任的权利。
- Commercial use: Please contact customer service for detailed consultation.<br>
- 商业使用：请联系客服进行细节咨询。

# Contact Us/联系我们

- Customer service Email/客服邮箱：<info@clklog.com>

- Customer service mobile/客服手机：16621363853

- Customer service WeChat/客服微信：opensoft66

- Customer service QR Code/客服二维码：<img title="" src="docs/assets/imgs/contactqrcode.jpg" alt="" data-align="center" width="120" style="vertical-align:top">
