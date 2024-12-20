
## 1.基础事件的集成

在使用ClkLog作为神策SDK（如WEB JS、Android&iOS、React-Native等客户端）的数据接收端时，需注意以下几点

### 1.1.会话的集成

  ClkLog的基础流量统计指标中的**访问次数**、**平均访问页数**、**访问时长**、**用户画像列表**都与会话有关，所以集成SDK后首先需要排查会话是否集成。
  
  如果采集的日志中事件属性包含 `$event_session_id` 属性并且有值，则说明会话集成成功。

   会话的集成方法：

   1）直接开启：如 Android&iOS、React-Native ([react-native-demo](https://gitee.com/clklog/clklog-react-native-demo)) 的 SDK 可直接`enableSession`。

   2）集成插件：如 web js sdk 需集成 session-event ([webjs-demo](/integration/reference.md#1-web-js-埋点集成参考))。

   3）自己实现：如 uni-app sdk （[uni-app-deme](https://gitee.com/clklog/clklog-uniapp-demo)） 、 unity3d sdk ([unity3d-demo](https://gitee.com/clklog/clklog-unity3d-demo))。

   **其他端的SDK中会话的集成请参考神策官方文档或查看SDK源码。**

### 1.2.浏览页面事件的集成

ClkLog的基础浏览统计指标中的**浏览量**指标基于**浏览页面事件**。

由于神策不同客户端的SDK的浏览页面事件的事件名称不同，ClkLog根据神策SDK的浏览页面事件定义对各种端做了不同的统计分析处理。

**神策各端SDK的浏览页面事件如下**

- Web：`$pageview`
- Android&iOS：`$AppViewScreen`
- 小程序：`$MPViewScreen`
- 其他端: `ClkViewScreen`
  - `ClkViewScreen`事件是ClkLog**付费版**中定义的除Web、Android&iOS、小程序端以外的其他端的SDK想要进行受访页面统计分析的通用事件。
  - [`ClkViewScreen`事件集成方法参考](/tutorials/ClkViewIntegrated.md)

**在实际应用过程中请根据不同版本的SDK去查询相关的事件数据, 如果在ClkLog统计前端浏览量无数据请根据埋点项目所引用的SDK去排查采集到的日志记录是否有相应的浏览页面事件。**
>
> [!tip]
>**以下功能目前仅在ClkLog付费版中支持**

## 2.登录用户的集成

当用户登录时，需主动调用神策SDK的 login 接口，将用户的注册信息（一般为用户在业务系统中的唯一身份标识）传入。

Web JS用户登录接口示例：

```
sensors.login('login_user_id');
```

[神策Web JS（简易用户关联（Web）参考链接](https://manual.sensorsdata.cn/sa/latest/zh_cn/web-109576379.html)

更多其他端（如Andriod、IOS、微信小程序等）用户登录集成代码接入方式请参考神策官方SDK集成文档。

## 3.自定义事件/用户属性集成

**在集成自定义事件/用户属性之前需先在【元数据管理】处添加或导入事件、事件属性/用户属性。**

### 3.1.元数据管理

元数据是按项目根据业务系统定义的自定义事件、神策预置事件的事件及属性（事件、用户、公共）的管理。

**在集成前端自定义事件/用户属性的代码埋点之前，需要先在【元数据管理】处下载预置事件、预置属性、用户属性相关模板，并根据实际情况定义好要采集的事件（预置和自定义）、事件属性（预置和自定义）、用户属性（预置和自定义）添加或导入【ClkLog后台】-【元数据管理】相关元数据后在进行代码埋点，否则SDK全埋点相关的事件日志不会被同步至事件表，进而导致ClkLog后台的【自定义分析】功能统计的事件数量和原始采集的日志数据量不一致。**

[元数据相关名词解释](/integration/method.md#_31相关名词解释)

[自定义事件集成步骤参考](/integration/method.md#_33事件分析集成步骤)

[自定义事件代码埋点参考](/integration/method.md#_1-自定义事件（代码埋点）)

[预置事件代码埋点参考](/integration/method.md#_2-预置事件（全埋点）)

[公共属性代码埋点参考](/integration/method.md#_3-公共属性埋点)

[用户属性代码埋点参考](/integration/method.md#_3-用户属性埋点)

### 3.2.日志查询

日志查询功能用于帮助开发者在事件分析相关埋点代码集成成功后，查看埋点的相关事件及其属性是否成功采集。
>
> [!tip]
>**日志查询中有数据的前提条件：<br>
1、ClkLog的receiver服务可以正常接收客户端采集的日志数据并存入kafka。<br>
2、ClKLog的processing服务可以正常依托flink，消费kafka数据并存入clickhouse。<br>
3、ClkLog的api服务可以正常访问。**

**日志查询数据可埋点日志数据不一致可能出现的问题和排查方法如下：**

Q1：控制台有事件日志，但日志查询中无数据。<br>
A1：请检查：在【元事件管理-元事件】中是否定义相关事件并关联相关属性。

Q2: 日志查询中的事件属性和控制台打印的事件属性数量不一致。<br>
A2：请检查：1、在【元事件管理-事件属性】中是否定义相关属性；2、在【元事件管理-元事件】中是否将事件属性和事件进行关联。

Q3：埋点设置用户属性后控制台有用户属性，但日志查询中的事件没有用户属性。<br>
A3：请检查：1、用户登录后是否设置用户ID（用户ID不能是androidid之类的关键词）。2、在【元事件管理-用户属性】中未定义项目的用户属性（用户属性不能是label之类的关键词）。
