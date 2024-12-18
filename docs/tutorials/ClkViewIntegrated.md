神策不支持全埋点的SDK（如C++、 C#等）如何在ClkLog中实现用户访问的基础数据统计分析？

## 方案背景

ClkLog 基础统计分析功能是基于神策SDK的全埋点从访问用户的访问渠道、访问设备、访问地域、访客类型、访问来源等维度对用户的访问量、访问次数、访问时长、访问页面深度等数据进行展示与分析，以满足基础的用户访问统计和用户画像功能。<br>

但神策SDK（如C++、 C#等）仅提供自定义事件埋点，并未提供全埋点相关功能，使用有ClkLog的用户埋点的前端应用是使用C++、C#等语言来实现的，也希望能够通过ClkLog来实现用户的访问的基础数据统计分析该如何实现了？针对有此类型需求的应用ClkLog也做了相应的技术方案的实现。<br>

ClkLog定义了一个专有的自定义事件“ClkViewScreen”，提取了实现基础访问统计分析必须要实现的属性，在应用中通过神策SDK的自定义事件埋点的API来实现“ClkViewScreen”事件及其相关属性的赋值。完成事件“ClkViewScreen”的埋点后，进入ClkLog前端即可查看相关SDK埋点的数据日志及统计分析结果。

## 方案介绍

**以下方案目前仅支持ClkLog付费版。**

### 1）导入事件模板

下载模板“<a href="/res/Clklog基础访问统计事件模板.xlsx" target="_blank" rel="noopener" id="clkViewScreenTemplateDownload">Clklog基础访问统计事件模板.xlsx</a>” 并导入【ClkLog-事件分析-元数据管理-元事件】

### 2）自定义代码埋点

通过神策sdk的自定义埋点事件api实现模板中的“ClkViewScreen”事件埋点，“ClkViewScreen”事件的触发时机一般是进入应用页面时，事件“ClkViewScreen”相关属性值说明及赋值方法实现逻辑如下：

| 属性英文名        | 属性显示名       | 说明                           |
| ----------------- | ---------------- | ------------------------------ |
| endpoint          | 终端             | 根据应用自定义                 |
| $user_agent       | UserAgent        | 用户代理                       |
| $referrer         | 向前地址         | 用于分析访问来源               |
| $referrer_host    | 向前域名         | 用于分析访问来源host           |
| $url              | 页面地址         | 用于收集并分析受访页面访问数据 |
| $url_path         | 页面路径         | 用于收集并分析受访页面访问数据 |
| $title            | 页面标题         | 用户收集并分析页面标题         |
| $is_first_day     | 是否首日访问     | 用于分析访问用户是否为新用户   |
| $is_first_time    | 是否首次触发事件 | 标记是否首次触发事件           |
| $event_session_id | 会话ID           | 用于对用户访问的会话进行切割   |

#### 属性赋值说明

- 1) 基础属性的赋值

    上述表格中endpoint、$user_agent、$referrer、$referrer_host、$url、$url_path、$title属性赋值请根据实际应用情况赋值。

- 2) $is_first_day 的实现
  
    是否首日访问采集，[赋值方法实现逻辑参考](https://manual.sensorsdata.cn/sa/latest/zh_cn/%E6%96%B0%E5%A2%9E%E7%94%A8%E6%88%B7%E5%8F%8A%E9%A6%96%E6%97%A5%E9%A6%96%E6%AC%A1%E6%A0%87%E8%AE%B0-136118554.html#id-.%E6%96%B0%E5%A2%9E%E7%94%A8%E6%88%B7%E5%8F%8A%E9%A6%96%E6%97%A5%E9%A6%96%E6%AC%A1%E6%A0%87%E8%AE%B0v2.5-%E6%96%B0%E5%A2%9E%E7%94%A8%E6%88%B7%E6%A0%87%E8%AE%B0%E9%87%87%E9%9B%86)

- 3) $is_first_time 的实现

    是否首次触发事件，[赋值方法实现逻辑参考](https://manual.sensorsdata.cn/sa/latest/zh_cn/%E6%96%B0%E5%A2%9E%E7%94%A8%E6%88%B7%E5%8F%8A%E9%A6%96%E6%97%A5%E9%A6%96%E6%AC%A1%E6%A0%87%E8%AE%B0-136118554.html#id-.%E6%96%B0%E5%A2%9E%E7%94%A8%E6%88%B7%E5%8F%8A%E9%A6%96%E6%97%A5%E9%A6%96%E6%AC%A1%E6%A0%87%E8%AE%B0v2.5-%E6%96%B0%E5%A2%9E%E7%94%A8%E6%88%B7%E6%A0%87%E8%AE%B0%E9%87%87%E9%9B%86)

- 4) $event_session_id 的实现
  
   $event_session_id的赋值需要实现session的切割。  

   session切割的实现原理：

    1）设置session时间间隔
    默认时间设置：一般Web网站的会话（session）过期时间为30分钟，App的会话（session）过期时间为1分钟。

    2）对session进行切割
    切割规则：用户在设定的session时间间隔内的所有动作使用同一个session_id，若在时间间隔内没有做任何操作，那间隔时间外的下一次的操作则生成一个新的session_id。

- 5) anonymous_id的实现

    anonymous_id：匿名ID，即设备ID，在神策中的概念参考详见“[标识用户——简易用户关联（IDM2.0&IDM1.0）v3.0-设备ID（匿名ID）](https://manual.sensorsdata.cn/sa/latest/zh_cn/idm-2-0-idm-1-0-185862540.html#id-.%E6%A0%87%E8%AF%86%E7%94%A8%E6%88%B7%E2%80%94%E2%80%94%E7%AE%80%E6%98%93%E7%94%A8%E6%88%B7%E5%85%B3%E8%81%94%EF%BC%88IDM2.0&IDM1.0%EF%BC%89v3.0-%E8%AE%BE%E5%A4%87ID%EF%BC%88%E5%8C%BF%E5%90%8DID%EF%BC%89)”

#### 日志示例代码

埋点成功后的日志参考示例代码如下

```
{
 "_track_id": 431270586,
 "distinct_id": "1000673537",
 "event": "ClkViewScreen",
 "lib": {
  "$lib": "cpp",
  "$lib_method": "code",
  "$lib_version": "1.0.3"
 },
 "properties": {
  "endpoint": "C++",
  "$is_login_id": true,
  "$lib": "cpp",
  "$lib_version": "1.0.3",
  "$user_agent": null,
  "$referrer": "",
  "$url": "http://192.168.100.208/clklog/",
  "$url_path": "/clklog/",
  "$title": "Vue & Sensors SDK2",
  "$is_first_day": false,
  "$is_first_time": false,
  "$referrer_host": "",
  "$event_session_id": "190bf7a940f139031354e9748451c26001f512073600190bf7a94101324"
 },
 "time": 1718191800586,
 "type": "track",
 "_flush_time": 1718191800586, 
 "anonymous_id": "1000673537" 
}

```

### 3）扩展配置

在clklog-api服务中扩展lib-type-map配置。
参考代码如下：

```
clklog-api:
  lib-type-map:
    all: all,全部
    android: Android,安卓
    ios: iOS,苹果
    js: js,网站
    miniprogram: MiniProgram,微信小程序
    harmonyos: HarmonyOS,鸿蒙
    dotnet: DotNET,DotNET
    cpp: cpp,cpp
    java: Java,Java
```

### 4）验证是否接入成功

事件“ClkViewScreen”接入并上报成功后，登录ClkLog前端数据概览，查看浏览量、访问次数、访客数、平均访问页数、平均访问时长、跳出率等数据是否正常加载。
