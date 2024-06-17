
## 1.集成方式简介

ClkLog数据采集是基于神策分析SDK来实现的，在实际应用过程中，首先要完成ClkLog相关应用和服务的安装与部署。在完成ClkLog的安装部署后，采集端应用通过神策分析官方提供的客户端SDK和服务端SDK对数据进行采集，将采集到的数据接收地址配置为ClkLog接收服务【clklog-receiver】的地址，经由ClkLog处理服务【clklog-processing】处理后，在ClkLog前端项目【clklog-ui】去调用统计接口【clklog-api】来实现埋点采集数据的统计分析数据图形化界面展示。

ClkLog数据采集基于神策分析SDK来实现，所以关于如何采用神策分析SDK进行客户端和服务端埋点的集成方式请参考神策分析官方文档。

为方便您的快速集成，在该文档我们整理了神策分析常规的SDK的下载和集成文件入口，以供参考，详细参考后续章节“<a href="/#/quickstart/integration.md#_3埋点集成sdk及文档">**3.埋点集成SDK及文档**</a>”内容。

## 2.核心功能集成介绍

### 2.1.项目管理

ClkLog商业版支持多个项目的数据采集，各个项目采集的数据从数据的查询、统计、分析及展示层面进行的数据隔离,每个项目可以由不同的账号进行管理，每个账号可以独立管理查看自己权限范围内的项目的埋点数据统计。

### 2.2.基础数据分析集成

ClkLog 基础统计分析功能基于访问页面相关事件（如：$pageview,
$WebStay, $WebPageLoad, $WebPageLeave），从访问用户的访问渠道、访问设备、访问地域、访客类型、访问来源等维度对用户的访问量、访问次数、访问时长、访问页面深度等数据进行展示与分析，可满足基础的用户访问统计和用户画像功能。此类相关的基础数据分析的集成只需要通过神策SDK进行代码全埋点，配置埋点数据采集接收地址为ClkLog接收服务地址即可。

全埋点集成方式参考：<a href="/#/quickstart/deployment.md#_11-sdk-埋点集成" target="clklogwebjssdkintegration">ClkLog 埋点集成</a>

### 2.3.简易用户关联集成

用户关联是为了对用户进行唯一标识，提高用户行为分析的准确性。ClkLog目前已集成了神策提供了简易用户关联。

Web JS用户登录集成方式参考神策官方集成方法，参考链接如下：

[简易用户关联（Web）](https://manual.sensorsdata.cn/sa/latest/zh_cn/web-109576379.html)

更多其他端（如Andriod、IOS、微信小程序等）用户登录集成代码接入方式请参考神策官方SDK集成文档。

### 2.4.事件分析集成

ClkLog的事件分析功能主要用于除与访问页面相关的事件外的其他相关的相关事件，如$AppStart 、$AppInstall以及记录用户行为或业务过程的事件的分析。和神策事件分析一样，ClkLog将此类事件分为两种，一种是预置事件，还有一种是自定义事件。因为事件是和用户的关联性，同时ClkLog也集成了神策的用户登录和用户属性。

对于事件（预置和自定义）、事件属性（预置和自定义）、用户属性（预置和自定义）在ClkLog的数据埋点和数据采集，需要注意的是在进行代码埋点之前我们必须先根据实际业务情况定义好要采集的事件（预置和自定义）、事件属性（预置和自定义）、用户属性（预置和自定义）添加或导入【ClkLog后台】-【元数据管理】相关库，然后通过神策SDK添加相关事件及属性的的代码埋点,代码埋点集成完毕上线后在才能在ClkLog后台进行统计分析。

#### 2.4.1.事件分析集成步骤

##### 2.4.1.1.下载数据采集模板

【ClkLog - 事件分析 - 数据采集模板】清单如下：

- **1）Clklog - 事件分析 - 自定义事件 - 数据采集模板.xlsx**

    登录【ClkLog后台】-【元数据管理】-【元事件】，点击列表上方的按钮【事件批量上传】，下载自定义事件采集模板。

- **2）Clklog - 事件分析 - 全埋点（预置）事件 - 数据采集模板.xlsx**

    登录【ClkLog后台】-【元数据管理】-【元事件】，点击列表上方的按钮【事件批量上传】处，下载全埋点（预置）事件采集模板。

- **3）Clklog - 事件分析 - 事件属性 - 数据采集模板.xlsx**

    登录【ClkLog后台】-【元数据管理】-【事件属性】，点击列表上方的按钮【事件属性批量上传】处，下载事件属性采集模板。

- **4）Clklog - 事件分析 - 用户（自定义）属性 - 数据采集模板.xlsx**

    登录【ClkLog后台】-【元数据管理】-【用户属性】，点击列表上方的按钮【用户属性批量上传】处，下载用户（自定义）属性采集模板。

- **5）Clklog - 事件分析 - 用户（预置）属性 - 数据采集模板.xlsx**

    登录【ClkLog后台】-【元数据管理】-【用户属性】，点击列表上方的按钮【用户属性批量上传】处，下载用户（预置）属性采集模板。

**注意：**

- Ø  自定义事件、自定义用户属性模板中的数据仅作参考，请根据项目实际情况去调整模板中的数据，数据项的填写请参考模板上方的提示。

- Ø  全埋点（预置）事件、事件属性、预置用户属性模板整理来源于神策官网预置事件和预置属性，仅包含App/Web/微信小程序通用预置事件和预置属性，如在使用过程中发现预置事件和属性有所缺失请参考集成的神策SDK版本对应的预置事件和预置属性文档，在ClkLog提供的模板基础上进行添加或修改相关事件及属性后重新导入或者直接在ClkLog后台元数据管理中进行手工录入。<br/>
更多全埋点事件列表请访问：[预置事件与预置属性](https://www.sensorsdata.cn/manual/mp_sdk_manual.html)

##### 2.4.1.2.设计数据采集方案

在实际应用中，需要由业务方来定义业务需要看什么数据，根据实际业务需求，抽出需要分析的业务点以及业务点需要分析的属性数据，定义好业务事件的触发时机形成数据采集方案给到研发,研发根据业务需求，按照ClkLog提供的【事件分析 - 数据采集模板】整理好需要埋点采集的事件及事件属性、用户属性相关模板文件。

  **事件和属性命名规范如下：**

- **1)关于命名格式**

所有事件和属性英文明不能以数字开头，且只包含：大小写字母、数字、下划线和$。

神策的预置事件和预置属性，都以$符号开头。所有的自定义事件和属性，不能包含$符合。

自定义事件可引用同含义的预置属性的变量名，但需要使用代码埋点的方式手动传值。

- **2)中英文命名一一对应**

事件的中文名称必须和英文名称一一对应。所有的属性，包括不同事件中的属性与其英文名也必须一一对应。

- **3)属性尽量复用**

为了在性能上友好，在分析时也比较方便，当不同事件中的属性，表达的含义相同时，优先选择复用属性。

- **4)大小写不敏感**

神策的事件名和属性名大小写不敏感，但其限制方式比较特殊，即一旦有一个变量名被使用，其他和该名称仅有小大写区分的变量名就不允许创建。因此，再设计和后期维护时，需要避免此类现象发生。

如：已有app_name属性后，App_name 未key上报属性的事件就无法入库。

- **5)避免与保留属性名冲突**

为了保证查询时属性名不与系统变量名冲突，设置如下保留属性名，避免其作为自定义属性名使用：

date、datetime、distinct_id、event、events、first_id、id、original_id、device_id、properties、second_id、time、user_id、users。

##### 2.4.1.3.导入数据采集元数据

   登录ClkLog后台【元数据管理】，将整理好的数据采集模板导入对应项目的元数据（事件、事件属性、用户属性）表中。

   **注意：**

- Ø  在ClkLog元数据表中，元数据是跟项目有关的，每个项目有自己独立的元数据定义，在导入过程中请注意确认元数据导入的项目。

- Ø  在导入过程中，建议将自定义事件、用户属性和预置事件、预置属性分开整理导入。

- Ø  属性值数据类型由首次导入时的类型决定，后续修改数据类型重新导入不会更新数据类型。

- Ø  通过事件模板导入系统后，修改导入文件里的事件英文变量名/属性英文变量名再次重新导入则会导入一个新的事件/属性名称，不会修改之前导入的事件/属性信息。

- Ø  重复多次导入事件及属性，事件/属性数据只做追加不做删除，如需删除多余事件/属性请在【ClkLog后台-元数据管理-事件/属性管理】界面做删除。

##### 2.4.1.4.添加事件代码埋点

###### 1) 自定义事件（代码埋点）

神策SDK 初始化后，即可以通过 **track()** 方法追踪用户行为事件，并添加自定义属性。

自定义事件, 代码参考示例：

`
sensors.track("newevent",{"prop1":"hello","prop2":"world"});
`

Web JS自定义事件代码埋点集成方式参考神策官方集成方法，参考链接如下：

[自定义事件集成文档（Web）](https://manual.sensorsdata.cn/sa/latest/zh_cn/web-7545024.html#id-%E9%9B%86%E6%88%90%E6%96%87%E6%A1%A3%EF%BC%88Web%EF%BC%89-%E4%BB%A3%E7%A0%81%E5%9F%8B%E7%82%B9%E8%BF%BD%E8%B8%AA%E4%BA%8B%E4%BB%B6)

更多其他端（如Andriod、IOS、微信小程序等）自定义事件集成代码接入方式请参考<a href="https://manual.sensorsdata.cn/sa/latest/zh_cn/tech-1573416.html"  target="satechguide">神策官方SDK集成技术指南</a>。

###### 2) 预置事件（全埋点）

预置事件是神策SDK预定义的事件，在埋点集成之前，需先在【ClkLog后台】-【元数据管理】-【元事件】，添加或导入需要集成分析的预置事件及属性信息，然后根据神策SDK相应版本的集成说明开启相关事件功能。

预置事件和预置属性说明参考神策官方文档，参考链接如下：

[神策预置事件与预置属性说明](https://manual.sensorsdata.cn/sa/latest/zh_cn/tech_sdk_preset_events_and_preset_properties-7542128.html)

各端（如Andriod、IOS、微信小程序等）预置事件开启方法请参考<a href="https://manual.sensorsdata.cn/sa/latest/zh_cn/tech-1573416.html"  target="satechguide">神策官方SDK集成技术指南</a>。

###### 3) 用户属性埋点

用户属性埋点集成之前，需先在【ClkLog后台】-【元数据管理】-【用户属性】，添加或导入用户相关属性信息，然后根据神策SDK进行用户属性集成。

预置用户属性说明参考神策官方文档，参考链接如下：
[神策预置用户属性说明](https://manual.sensorsdata.cn/sa/latest/zh_cn/tech_sdk_all_preset_properties-152305885.html#id-.%E9%A2%84%E7%BD%AE%E5%B1%9E%E6%80%A7%E6%80%BB%E8%A1%A8%E6%A0%BCv3.0-%E9%A2%84%E7%BD%AE%E7%94%A8%E6%88%B7%E5%B1%9E%E6%80%A7)

Web JS自定义事件代码埋点集成方式参考神策官方集成方法，参考链接如下：
[用户属性埋点集成文档（Web）](https://manual.sensorsdata.cn/sa/latest/zh_cn/api-web-7538088.html#id-%E5%9F%BA%E7%A1%80API%E4%BB%8B%E7%BB%8D%EF%BC%88Web%EF%BC%89-%E8%AE%BE%E7%BD%AE%E7%94%A8%E6%88%B7%E5%B1%9E%E6%80%A7)

更多其他端（如Andriod、IOS、微信小程序等）用户属性集成代码接入方式请参考<a href="https://manual.sensorsdata.cn/sa/latest/zh_cn/tech-1573416.html" target="satechguide">神策官方SDK集成技术指南</a>。

###### 3) 公共属性埋点

公共属性是对于所有事件都需要添加的属性，在公共属性埋点集成之前，需先在【ClkLog后台】-【元数据管理】-事件属性，添加或导入事件的公共属性信息，然后根据神策SDK进行公共属性集成。

Web JS事件公共属性集成方式参考神策官方集成方法，参考链接如下：
[设置事件公共属性（Web）](https://manual.sensorsdata.cn/sa/latest/zh_cn/tech_sdk_client_web_api-7538088.html#id-%E5%9F%BA%E7%A1%80API%E4%BB%8B%E7%BB%8D%EF%BC%88Web%EF%BC%89-%E8%AE%BE%E7%BD%AE%E4%BA%8B%E4%BB%B6%E9%9D%99%E6%80%81%E5%85%AC%E5%85%B1%E5%B1%9E%E6%80%A7)

更多其他端（如Andriod、IOS、微信小程序等）事件公共属性集成代码接入方式请参考<a href="https://manual.sensorsdata.cn/sa/latest/zh_cn/tech-1573416.html" target="satechguide">神策官方SDK集成技术指南</a>。

#### 2.4.2.查看事件埋点数据采集结果

##### 1) 日志查询

事件分析相关埋点代码集成功后，在【ClkLog后台】-【事件分析】-【日志查询】查看相关事件埋点是否成功采集。

##### 2) 数据统计

事件分析相关埋点代码集成功后，在【ClkLog后台】-【事件分析】-【数据统计】查看相关事件埋点触发情况。

事件分析数据统计功能包含以下功能：

- a) 选定时间（今日/昨日/过去7天/30天/自定义）范围内的事件基础指标数据统计, 如总次数、用户数、人均次数。<br/>

- b) 按日、周、月查看指定事件的指标（总次数、用户数）的访问变化趋势以及事件触发详细日志记录。

## 3.埋点集成SDK及文档

### 客户端 SDK

#### Web JS SDK

| SDK下载  | [GitHub - sensorsdata/sa-sdk-javascript](https://github.com/sensorsdata/sa-sdk-javascript)   |
| -------- | -------------------------------------------------------------------------------------------- |
| 集成文档 | [Web JS SDK](https://manual.sensorsdata.cn/sa/latest/zh_cn/tech_sdk_client_web-1573905.html) |

#### IOS SDK

| SDK下载  | [](https://github.com/sensorsdata/sa-sdk-javascript)[GitHub - sensorsdata/sa-sdk-ios](https://github.com/sensorsdata/sa-sdk-ios) |
| -------- | -------------------------------------------------------------------------------------------------------------------------------- |
| 集成文档 | [iOS SDK](https://manual.sensorsdata.cn/sa/latest/zh_cn/tech_sdk_client_ios-1573911.html)                                        |

#### Andriod SDK

| SDK下载  | [GitHub - sensorsdata/sa-sdk-android](https://github.com/sensorsdata/sa-sdk-android)              |
| -------- | ------------------------------------------------------------------------------------------------- |
| 集成文档 | [Android SDK](https://manual.sensorsdata.cn/sa/latest/zh_cn/tech_sdk_client_android-1573908.html) |

#### 微信小程序 SDK

| SDK下载  | [GitHub - sensorsdata/sa-sdk-miniprogram](https://github.com/sensorsdata/sa-sdk-miniprogram) |
| -------- | -------------------------------------------------------------------------------------------- |
| 集成文档 | [小程序 SDK](https://manual.sensorsdata.cn/sa/latest/zh_cn/tech_sdk_client_mp-7537026.html)  |

### 服务端 SDK

| SDK下载  | [GitHub - sensorsdata/sa-sdk-java](https://github.com/sensorsdata/sa-sdk-java)  |
| -------- | ------------------------------------------------------------------------------- |
| 集成文档 | [Java SDK](https://manual.sensorsdata.cn/sa/latest/zh_cn/java-sdk-1573929.html) |

更多其他平台集成方式参考：<a href="https://manual.sensorsdata.cn/sa/latest/zh_cn/tech-1573416.html"  target="satechguide">神策官方SDK集成技术指南</a>
