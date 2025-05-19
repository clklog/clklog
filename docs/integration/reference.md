
## 1. Web JS 埋点集成参考

### 1.1. 下载 ClKLOG WEB JS SDK

  <a href="https://clklog.com/res/clklog.webjs.sdk.v3.zip" target="_blank" rel="noopener" id="webjssdkdownload">点击此处下载 CLKLOG WEB JS SDK</a>, 并将下载的`ClKLOG WEB JS SDK`文件包解压至网站目录, 参考目录结构如下：

  ```
  ├── 网站根目录    //SDK引用位置可根据实际应用情况进行调整   
      ├── plugins
      │   ├── session-event  //必须引用
      │   │   ├── index.js
      |── sensorsdata.js   
      |── autotrack.js
  ```

### 1.2. 修改`autotrack.js`中的配置信息

- 1）确认`autotrack.js`中`sensorsdata.js`和`plugins/session-event/index.js`在实际项目中的引用路径。

- 2）将`autotrack.js`中的`server_url` 接收服务地址配置信息修改为clklog数据采集地址，并调整参数`project`和`token`的配置。其中`project`名称默认为`clklogapp`，如果要修改`project`名称请注意调整`clklog-receiver`服务中`project-list`的相关配置。

    `autotrack.js`中的`server_url`参考配置如下：

    ```
      //server_url地址为 clklog-receiver 的接收服务地址
      server_url: 'http://10.10.222.21/receiver/api/gp?project=clklogapp&token=5388ed7459ba4c4cad0c8693fb85630a', 
    ```

- ##### **server_url 地址参数说明：**
  >
  >1. server_url地址为 clklog-receiver 的接收服务地址, project 和token参数必须传入。
  >
  >2. project 是埋点项目的项目编码，一般为英文字符，默认为clklogapp，可根据实际情况修改为自己项目的编码。
  >
  >3. token是receiver接收埋点项目日志的令牌，在【ClkLog后台】-【系统设置】-【项目管理】处添加项目后自动生成，receiver端在接收日志时暂未做任何验证。

- ##### **project 名称修改为自己项目的编码后，请注意执行以下修改或操作，否则日志不会入库：**
  <!-- >
  > 社区版：
  >
  > 1. 修改clklog-receiver服务中的project-list配置，project-list对应前端埋点代码配置的project名称，多个project以逗号分隔。
  >
  > 2. 修改clklog-ui 中的config.js 中的项目配置代码。
  >
  > 付费版：
  > -->
  > 在【ClkLog后台】-【系统设置】-【项目管理】处添加项目相关信息。

- ##### **单页面应用数据采集说明**

  1）如果是单页面应用，标题不变但需要自动采集页面浏览事件，需要将`autotrack.js`中的`is_track_single_page`值设置为`ture`。

  2）如果是单页面应用，标题会随着页面变化，同时也需要采集页面浏览事件，需要将`autotrack.js` 中的`is_track_single_page` 值设置为`false`，同时在页面标题改变结束后执行代码：  `sensors.quick('autoTrackSinglePage');`

> [!tip]
> 如果您是直接从神策官网下载的sdk并按照官网引用方法进行埋点的，请注意session-event的引用。

### 1.3. 接入埋点跟踪代码

  在web网站页面引用`autotrack.js`,参考代码如下。

   ```
    <script src="/autotrack.js"></script> 
   ```

### 1.4. 测试埋点代码是否接入正常

  在本地启动网站，打开浏览器访问网站，打开开发者工具，查看控制台, 出现如下提示信息说明埋点代码接入成功。

  ![](../assets/imgs/autotrack.png)

- **备注：locahost或ip访问时可忽略控制台中的 “[web-sdk-log]: getHostname传入的url参数不合法！” 提示**

### 1.5 验证埋点接入是否成功

- 埋点代码接入成功后，等待1分钟，返回前端 <http://YOUR_DOMAIN/> ，刷新数据概览页面，如下图所示，当页面上的浏览概览相关数据值开始有数据说明埋点接入成功。
   ![image](../assets/imgs/clklogindex.png)  

## 2. IOS 埋点集成参考

| SDK下载  | [GitHub - sensorsdata/sa-sdk-ios](https://github.com/sensorsdata/sa-sdk-ios)                                                           |
| -------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| 集成文档 | [iOS 集成文档](https://manual.sensorsdata.cn/sa/latest/zh_cn/tech_sdk_client_ios-1573911.html)                                         |
| 注意事项 | ClkLog接收端与神策无关，在按照神策iOS SDK集成文档集成的过程中，直接跳过配置 Scheme 步骤，将数据接收地址配置成clklog-receiver地址即可。 |
| 参考示例 | 暂无                                                                                                                                   |

## 3. Andriod 埋点集成参考

| SDK下载  | [GitHub - sensorsdata/sa-sdk-android](https://github.com/sensorsdata/sa-sdk-android)                                                       |
| -------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| 集成文档 | [Android 集成文档](https://manual.sensorsdata.cn/sa/latest/zh_cn/tech_sdk_client_android-1573908.html)                                     |
| 注意事项 | ClkLog接收端与神策无关，在按照神策Android SDK集成文档集成的过程中，直接跳过配置 Scheme 步骤，将数据接收地址配置成clklog-receiver地址即可。 |
| 参考示例 | 暂无                                                                                                                                       |

## 4. 微信小程序 埋点集成参考

| SDK下载  | [GitHub - sensorsdata/sa-sdk-miniprogram](https://github.com/sensorsdata/sa-sdk-miniprogram)     |
| -------- | ------------------------------------------------------------------------------------------------ |
| 集成文档 | [小程序 集成文档](https://manual.sensorsdata.cn/sa/latest/zh_cn/tech_sdk_client_mp-7537026.html) |
| 参考示例 | 暂无                                                                                             |

## 5. uni-app 埋点集成参考

| SDK下载  | [sensorsdata/uni-app-sdk](https://ext.dcloud.net.cn/plugin?id=4177)                      |
| -------- | ---------------------------------------------------------------------------------------- |
|          | [sensorsdata/uni-app-native-plugin](https://ext.dcloud.net.cn/plugin?id=4179)            |
| 集成文档 | [uni-app 集成文档](https://manual.sensorsdata.cn/sa/3.0/zh_cn/uni-app-js-109576938.html) |
| 参考示例 | [clklog-uniapp-demo in github](https://github.com/clklog/clklog-uniapp-demo)             |
|          | [clklog-uniapp-demo in gitee](https://gitee.com/clklog/clklog-uniapp-demo)               |
|          | [clklog-uniapp-demo in gitcode](https://gitcode.com/clklog/clklog-uniapp-demo)           |

## 6. react-native 埋点集成参考

| SDK下载  | [sensorsdata/react-native-sensors-analytics](https://github.com/sensorsdata/react-native-sensors-analytics)  |
| -------- | ------------------------------------------------------------------------------------------------------------ |
| 集成文档 | [react-native 集成文档](https://manual.sensorsdata.cn/sa/3.0/zh_cn/tech_sdk_client_three_react-1574002.html) |
| 参考示例 | [clklog-react-native-demo in github](https://github.com/clklog/clklog-react-native-demo)                     |
|          | [clklog-react-native-demo in gitee](https://gitee.com/clklog/clklog-react-native-demo)                       |
|          | [clklog-react-native-demo in gitcode](https://gitcode.com/clklog/clklog-react-native-demo)                   |

## 7. flutter 埋点集成参考

| SDK下载  | [sensorsdata/sensors_analytics_flutter_plugin](https://pub.dev/packages/sensors_analytics_flutter_plugin) |
| -------- | --------------------------------------------------------------------------------------------------------- |
| 集成文档 | [flutter 集成文档](https://manual.sensorsdata.cn/sa/latest/flutter-1574005.html)                          |
| 参考示例 | [clklog-flutter-demo in github](https://github.com/clklog/clklog-flutter-demo)                            |
|          | [clklog-flutter-demo in gitee](https://gitee.com/clklog/clklog-flutter-demo)                              |
|          | [clklog-flutter-demo in gitcode](https://gitcode.com/clklog/clklog-flutter-demo)                          |

## 8. unity3d 埋点集成参考

| SDK下载  | [sensorsdata/sa-sdk-unity](https://github.com/sensorsdata/sa-sdk-unity)               |
| -------- | ------------------------------------------------------------------------------------- |
| 集成文档 | [unity3d 集成文档](https://manual.sensorsdata.cn/sa/docs/tech_sdk_client_unity/v0300) |
| 参考示例 | [clklog-unity3d-demo in github](https://github.com/clklog/clklog-unity3d-demo)        |
|          | [clklog-unity3d-demo in gitee](https://gitee.com/clklog/clklog-unity3d-demo)          |
|          | [clklog-unity3d-demo in gitcode](https://gitcode.com/clklog/clklog-unity3d-demo)      |
