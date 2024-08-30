
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
      //接收地址为clklog-receiver 的接收服务地址，project和token参数必须传入，token是每个project对应的随机字符串，请自行随机生成。
      server_url: 'http://10.10.222.21/receiver/api/gp?project=clklogapp&token=5388ed7459ba4c4cad0c8693fb85630a', 
    ```

- **单页面应用数据采集说明**

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

## 2. Android SDK埋点集成

  集成方式参考 [神策Android SDK集成文档](https://manual.sensorsdata.cn/sa/latest/zh_cn/android-7541696.html) , 集成时将数据接收地址更换成clklog-receiver的接收服务地址。

## 3. IOS SDK埋点集成

  集成方式参考 [神策IOS SDK集成文档](https://manual.sensorsdata.cn/sa/latest/zh_cn/ios-7538614.html) , 集成时将数据接收地址更换成clklog-receiver的接收服务地址。

## 4. 小程序 SDK埋点集成

  集成方式参考 [神策微信小程序 SDK集成文档](https://manual.sensorsdata.cn/sa/latest/zh_cn/%E9%9B%86%E6%88%90%E6%96%87%E6%A1%A3%EF%BC%88%E5%BE%AE%E4%BF%A1%E5%B0%8F%E7%A8%8B%E5%BA%8F%EF%BC%89-1573892.html) , 集成时将数据接收地址更换成clklog-receiver的接收服务地址。
