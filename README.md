<p align="center"><img src="https://clklog.com/assets/imgs/logo1.png" height="60"/> </p>
<p align="center">开源 + 私有化部署的埋点用户行为分析系统  </p> <p align="center"> 支持多平台埋点采集、主流分析模型及国产化/信创环境适配 </p>
<p align="center"><a href="https://clklog.com/" target="_blank">官网</a> | <a href="https://clklog.com/product/intro.html" target="_blank">产品介绍</a> | <a href="https://clklog.com/install/intro.html" target="_blank"> 技术指南</a>
</p>
<p align="center"> <img src="https://img.shields.io/github/stars/clklog/clklog?style=for-the-badge&color=ff6b6b"/>
<img src="https://img.shields.io/github/forks/clklog/clklog?style=for-the-badge&color=4dabf7"/>
<!-- <img src="https://img.shields.io/github/issues/clklog/clklog?style=for-the-badge&color=20c997"/> -->
<img src="https://img.shields.io/github/license/clklog/clklog?style=for-the-badge"/> </p>

## ✨ ClkLog是什么

ClkLog是一款轻量级埋点用户行为分析平台，支持私有化部署与源码交付，帮助企业快速构建完整、私有的用户行为分析体系。产品提供事件分析、漏斗分析、留存分析、路径分析、用户画像、用户分群等能力，支持 Web、App、小程序等多平台数据采集。

ClkLog采用开源 + 商业版本的模式，所有版本都支持完整源码交付，目前包含社区版（AGPL-3.0 协议）以及商业授权的专业版、企业版和信创版本，并持续推进国产化与信创环境适配。

目前ClkLog已应用于汽车、电力、金融、保险、房产、物流等多个行业场景，帮助企业开展用户行为分析与数字化运营工作。

## 🚀 核心优势

### 1. 私有化部署，数据自主可控

企业无需将数据上传第三方平台,支持本地部署、内网部署、混合部署、安全隔离环境, 企业自主可控，满足数据安全合规要求。

### 2. 多平台埋点采集

支持多端（Web/H5/App/小程序/鸿蒙）埋点数据采集，涵盖自定义事件、用户标识关联,实现统一用户行为数据体系。

### 3. 主流分析模型开箱即用

内置访问分析、事件分析、漏斗分析、用户画像、标签体系、分群分析，无需自行开发分析引擎，可快速建立数据分析能力。

### 4. 国产化 / 信创环境适配

支持企业信创建设场景。适配国产 CPU、国产服务器、国产操作系统、国产数据库，适用于有信创需求的企业单位。

<!-- ## 🔥 核心功能

基础访问分析：多维度掌握用户访问情况，快速洞察流量结构与用户行为。
多维事件分析：围绕关键业务行为，灵活配置事件埋点，分析用户行为轨迹。
用户画像分析：沉淀用户特征数据，支持标签、分群与用户行为细查，辅助私域运营。 -->

## 📦版本体系

ClkLog 提供多版本能力体系，满足不同企业阶段需求。

| 版本         | 定位                           |
| ------------ | ------------------------------ |
| 🟢 开源社区版 | 适合开发者 / 学习 / 小团队     |
| 🔵 PRO专业版  | 适合用户访问行为分析与运营场景 |
| 🟣 CDP企业版  | 适合用户画像与精细化运营场景   |
| 🟡 信创版     | 适合政企信创国产私有化场景     |

### 🧩 版本能力对比

| 能力       | 社区版 | 专业版 | 企业版 | 信创版 |
| ---------- | ------ | ------ | ------ | ------ |
| 数据采集   | ✅      | ✅      | ✅      | ✅      |
| 事件分析   | 基础   | 完整   | 完整   | 完整   |
| 用户画像   | 基础   | 基础   | 完整   | 完整   |
| 漏斗分析   | ❌      | ✅      | ✅      | ✅      |
| 用户细查   | ❌      | ❌      | ✅      | ✅      |
| 标签体系   | ❌      | ❌      | ✅      | ✅      |
| 信创适配   | ❌      | ❌      | ❌      | ✅      |
| 私有化部署 | ✅      | ✅      | ✅      | ✅      |

## 🏗 系统架构

ClkLog 采用分层式架构设计。

```text
SDK/埋点采集（Web/App/小程序）
        │
        ▼
Receiver（数据接收）
        │
        ▼
Kafka（消息缓冲）
        │
        ▼
Processing（数据处理）
        │
        ▼
ClickHouse / Doris（数据存储）
        │
        ▼
API + 管理后台
        │
        ▼
统计分析与可视化
 
```

## 🧰 技术栈

ClkLog 采用主流企业级技术架构，支持高并发与可扩展分析能力。

后端：Java / Redis / Kafka / Zookeeper

前端：Vue / ElementUI / ECharts

数据库：ClickHouse / Apache Doris、MySQL / OpenGauss / OceanBase

## 系统示意图

| ![](https://clklog.com/assets/imgs/preview/com/1.png) 社区版-数据概览     | ![](https://clklog.com/assets/imgs/preview/com/2.png) 社区版-趋势分析       | ![](https://clklog.com/assets/imgs/preview/com/3.png)  社区版- 地域分析       |
| ------------------------------------------------------------------------- | --------------------------------------------------------------------------- | ----------------------------------------------------------------------------- |
| ![](https://clklog.com/assets/imgs/preview/pro/10.png) PRO专业版-留存分析 | ![](https://clklog.com/assets/imgs/preview/pro/13.png) PRO专业版-自定义分析 | ![](https://clklog.com/assets/imgs/preview/pro/14.png) PRO专业版-漏斗分析     |
| ![](https://clklog.com/assets/imgs/preview/cdp/2.png) CDP企业版-用户标签  | ![](https://clklog.com/assets/imgs/preview/cdp/8.png) CDP企业版-用户群画像  | ![](https://clklog.com/assets/imgs/preview/cdp/14.png) CDP企业版-用户行为细查 |

## 社区支持

欢迎：⭐ Star 🐞 Issue 🔧 PR 💬 交流建议
________________________________________

## License

### 开源协议：AGPL V3.0

• 使用 Clklog 的组织或个人在复制、分发、转发或修改时请遵守AGPL V3.0相关条款，不得移除ClkLog相关版权标识。任何分发或通过网络提供服务的版本（包括衍生版本）必须开源，并保留原版权和协议信息。如有违反，ClkLog将保留对侵权者追究责任的权利。

#### 免费使用

• 适用范围：个人开发者、学术研究及非商业项目可免费使用

• 商业限制：若将ClkLog集成到闭源商业产品中，任何修改、二开、集成须遵循 AGPLv3.0 协议开源衍生产品

• 授权方式：遵循 AGPLv3.0 协议

#### 商业使用

• 适用范围：商业项目集成可闭源使用

• 授权方式：需购买商业授权

### 特别提醒

在AGPL V3.0协议中，“衍生产品”是指：在 ClkLog 源代码基础上进行任何修改、扩展、适配、重构，或与其他软件、系统组合后形成的作品，包括但不限于：

• 修改、删除或新增源代码的版本；

• 增加功能模块、插件或集成接口的版本；

• 将 ClkLog 嵌入或整合进其他产品、系统或服务的版本；

• 改变数据结构、接口协议或运行架构的版本。

无论改动大小，只要衍生产品包含 ClkLog 的代码或核心逻辑，即视为衍生产品，并适用本协议的相关条款。

## 商业支持与合作

如需了解完整产品能力、信创方案或商业版本合作方式，欢迎通过以下方式联系我们获取详细资料：

- 联系电话：&nbsp;&nbsp; 16621363853

- 客服微信：&nbsp;&nbsp; opensoft66

- 微信客服: &nbsp;&nbsp;&nbsp;&nbsp; <img title="" src="https://clklog.com/assets/imgs/qrcode_contact.png" alt="" data-align="center" width="120" style="vertical-align:top">

- 微信公众号:&nbsp;&nbsp;<img title="" src="https://clklog.com/assets/imgs/contactqrcode.jpg" alt="" data-align="center" width="120" style="vertical-align:top">
