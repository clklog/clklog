<p align="center"><img src="https://clklog.com/assets/imgs/logo1.png" height="60"/> </p>
<p align="center">ClkLog|轻量级开源埋点用户行为分析平台  </p> <p align="center"> 支持 Web、App、小程序、HarmonyOS 等多平台数据采集，帮助企业构建自主可控的用户行为分析能力。 </p>
<p align="center" >全开源 · 私有化部署  · 企业级扩展 </p>

<p align="center">如果这个项目对您有帮助，欢迎点一个 ⭐ Star 支持我们！</p>

## 为什么会有ClkLog？
**做用户行为分析已是必然趋势。**

**越来越多企业开始重视用户行为数据，希望通过数据了解用户、优化产品、提升业务效率。**

但实际落地过程中，经常面临：
- SaaS 平台数据无法完全自主掌控；
- 商业分析产品成本较高，扩展受限；
- 自研用户行为分析平台周期长、维护成本高；
- 部分开源项目功能不完整，难以真正投入生产使用。

因此，我们创建了 ClkLog。

**我们希望 ClkLog 开源社区版，不是精简试用版，而是一个真正能被部署和使用的完整方案。**

ClkLog 致力于提供一套轻量级、私有化、完整的开源用户行为分析平台：

- 支持多端数据采集
- 提供完整的数据统计与分析能力
- 支持私有化部署
- 数据完全掌握在企业手中

让开发者和企业无需依赖第三方 SaaS，也能够快速构建属于自己的用户行为分析体系	


## ClkLog 可以帮助你什么？
部署 ClkLog（最快10分钟） 后，你可以快速拥有：

📥 数据采集

  支持：
- Web
- Android
- iOS
- 微信小程序
- uni-app

📊 数据分析
- PV、UV、访客数、访问次数等基础指标
- 趋势分析
- 地域分析
- 来源渠道分析
- 页面访问分析
- 设备与浏览器分析
- 新老访客分析
- 搜索词
- APP 崩溃分析

👤 用户洞察
- 基础用户画像
- 用户忠诚度分析

⚙️ 管理
- 项目管理
- 账号管理
- 密钥管理
	
让团队从"采集数据"到"理解用户"，快速搭建属于自己的用户行为分析平台。

## 为什么选择 ClkLog？

**轻量 · 开放 · 灵活 · 快速**

**数据自主可控**

私有化部署、数据掌握在企业内部、无需依赖第三方 SaaS、支持企业长期自主维护

**开放开源**

社区版开放源码、遵循 AGPL v3.0、支持二次开发、构建开放的数据分析能力

**灵活扩展**

支持源码扩展、支持业务系统集成、支持自定义开发、满足企业持续演进需求

**快速落地**

Docker 快速部署、多端 SDK 接入、开箱即用分析能力、快速构建用户行为分析平台


## 产品预览

|     ![](https://clklog.com/assets/imgs/preview/com/1.png) 社区版-数据概览     |      ![](https://clklog.com/assets/imgs/preview/com/2.png) 社区版-趋势分析      |      ![](https://clklog.com/assets/imgs/preview/com/3.png)  社区版- 地域分析      |
| :---------------------------------------------------------------------------: | :-----------------------------------------------------------------------------: | :-------------------------------------------------------------------------------: |
| ![](https://clklog.com/assets/imgs/preview/pro/10.png) **PRO专业版-留存分析** | ![](https://clklog.com/assets/imgs/preview/pro/13.png) **PRO专业版-自定义分析** |   ![](https://clklog.com/assets/imgs/preview/pro/14.png) **PRO专业版-漏斗分析**   |
| ![](https://clklog.com/assets/imgs/preview/cdp/2.png) **CDP企业版-用户标签**  | ![](https://clklog.com/assets/imgs/preview/cdp/8.png) **CDP企业版-用户群画像**  | ![](https://clklog.com/assets/imgs/preview/cdp/14.png) **CDP企业版-用户行为细查** |

## 立即体验

社区版 Demo ：https://demo.clklog.com

商业版 Demo ：https://pro.clklog.com


## 快速部署
ClkLog 支持多种部署方式，您可以根据实际环境选择适合的部署方案。

- Docker 部署：https://clklog.com/install/docker/intro.html

- 源码部署：https://clklog.com/install/source/preparation.html

更多部署方式、配置说明及常见问题，请参考官方文档


## 系统架构

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

## 技术栈

ClkLog 采用主流企业级技术架构，支持高并发与可扩展分析能力。

后端：Java / Redis / Kafka / Zookeeper

前端：Vue / ElementUI / ECharts

数据库：ClickHouse / Apache Doris、MySQL / OpenGauss / OceanBase

## ClkLog 持续成长						
我们坚持长期维护 ClkLog						
#### 已发布

✅ 多平台 SDK

✅ Docker 部署

✅ 用户行为分析

✅ SQL 分析

✅ API 开放能力

✅ 国产化适配

✅AI 数据助手（ AI Skill）	

#### 持续规划

🚧 全域用户关联

🚧 更多分析模型

🚧 更多 SDK

🚧 更多数据库支持

🚧 更多开放 API

🚧 AI 数据助手持续升级

🚧 更多国产化适配						

## ClkLog 商业版本（PRO / CDP / 信创）
除了社区版外，我们还提供更多商业版本，帮助企业构建更完整的数据分析体系。

例如：

🔹 用户关联（简易/全域）

🔹 自定义事件分析

🔹 漏斗分析

🔹 用户细查

🔹 标签体系

🔹 用户分群

🔹 国产数据库适配

🔹 专属技术支持

更多能力请访问官网：https://clklog.com/

## 社区
官方网站：https://clklog.com

技术支持文档：https://clklog.com/install/intro.html

Gitee：https://gitee.com/clklog

GitHub：https://github.com/clklog

欢迎进入社群或了解更多ClkLog 的能力：
- 客服邮箱：&nbsp;&nbsp;info@clklog.com

- 客服手机：&nbsp;&nbsp;16621363853
  
- 微信客服: &nbsp;&nbsp;<img title="" src="https://clklog.com/assets/imgs/qrcode_contact.png" alt="" data-align="center" width="120" style="vertical-align:top">

- 微信公众号:<img title="" src="https://clklog.com/assets/imgs/contactqrcode.jpg" alt="" data-align="center" width="120" style="vertical-align:top">



## License				
ClkLog 社区版采用 【AGPL v3.0】开源协议，并提供商业授权。

**开源使用**：免费使用，需要遵守AGPL v3.0	

**闭源使用**：购买商业授权		

### **社区版免费使用（AGPL v3.0 协议）**
				
任何组织或个人均可免费使用 ClkLog 社区版，包括但不限于：				
- 个人学习与开发
- 企业内部使用
- 学术研究
- 非商业项目
- 商业项目				
<p style="color:red">前提是必须遵守 AGPL v3.0 协议。</p>		

如果您基于 ClkLog 进行修改、二次开发或集成，并将软件分发给第三方，或通过网络向第三方提供服务，应按照 AGPL v3.0 协议履行相应义务，包括公开对应版本的源代码，并保留原有版权声明及 License 信息。				
				
				
				
### 什么时候需要购买商业授权？				
如果您希望在以下场景中使用 ClkLog，则建议购买商业授权：		

将 ClkLog 集成到闭源商业产品中；

对 ClkLog 进行二次开发，但不希望公开修改后的源代码；

基于 ClkLog 向客户交付闭源软件或解决方案；

购买商业授权后，可在授权范围内合法闭源使用，无需按照 AGPL v3.0 协议公开相关源代码。				
				
### 什么属于衍生产品？				
根据 AGPL v3.0 协议，基于 ClkLog 源代码形成的新作品通常属于衍生产品，包括但不限于：		
- 修改、删除或新增 ClkLog 源代码；
- 新增功能模块、插件或扩展能力；
- 调整系统架构、数据结构、接口协议或运行方式；
- 将 ClkLog 集成、嵌入或组合到其他软件、平台或业务系统中；
- 基于 ClkLog 进行二次开发形成的新版本。	

无论修改规模大小，只要最终产品包含 ClkLog 的源代码或基于其核心代码形成，通常都属于 AGPL v3.0 所约束的衍生产品，应遵守相应协议要求。				
				
### ClkLog提供商业授权				
除了社区版外，ClkLog 还提供更多商业版本（PRO / CDP / 信创）的授权，可支持：		

- 闭源商业使用；
- 100% 源码交付；
- 企业级功能；
- 国产化 / 信创适配；

官方技术支持与持续升级				
如需了解商业授权方案，请访问 ClkLog 官方网站或联系我们获取详细信息。				
				
## 支持 ClkLog				
如果您认可 ClkLog：

欢迎⭐ Star 项目，帮助更多开发者发现项目，也帮助我们持续投入版本迭代、文档完善和社区建设。				
