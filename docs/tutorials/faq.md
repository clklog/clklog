
## 埋点集成相关
>
> Q：receiver数据接收是不是一定要有ssl证书？
>
- A：不是。
<br><br>

>
> Q：接收服务地址从哪里获取？
>
- A：数据接收地址是部署的clklog-receiver服务中的数据接收服务地址，
  接收地址参考：http(s)://{YOUR_IPORDOMAIN}/receiver/api/gp?project={clklogapp}&token={apptoken}
<br><br>

> Q：token从哪里获取？

- A：在【ClkLog后台】-【系统设置】-【项目管理】处创建项目，设置下项目编码，获取项目token。
<br><br>

> Q：如何实现多应用/项目集成？

- A：在【ClkLog后台】-【系统设置】-【项目管理】处创建项目，设置下项目编码，获取项目token。
<br><br>

> Q：集成神策web js sdk的方式能否改用CommonJS方式，不用autotrack.js了吗？

- A：可以使用commonjs方式集成神策SDK，但还要以commonjs方式集成神策的session-event插件。[神策webjs sdk session-event下载地址](https://github.com/sensorsdata/sa-sdk-javascript/tree/master/dist/web/plugin/session-event)
<br><br>

> Q：接入神策Android SDK和IOS SDK时，文档上需要配置项目的Scheme，该如何处理？

- A：ClkLog接收端与神策无关，在按照神策SDK集成文档集成的过程中跳过配置 Scheme，直接将数据接收地址配置成clklog-receiver地址即可。
<br><br>

> Q：ClkLog是否支持自定义事件的代码埋点？

- A：社区版：不支持，可自行进行二次开发；商业版：支持。
<br><br>

## 统计指标相关

> Q：数据概览无法看到数据。

- A：如果数据概览所有指标项都没有数据，则需要先检查埋点数据是否接收成功；如果只是会话相关数据（访问次数、平均访问页数、平均访问时长、跳出率）没有，需要开启会话或集成会话相关插件。 [会话集成参考](/tutorials/notes.md#_11会话的集成)
<br><br>

>
> Q：实时访问-热门页面，区分是网页还是原生页面吗？

- A： 不区分。
<br><br>

> Q：Web前端项目埋点已集成，数据也采集到了，数据统计有浏览量、访客数、IP数，但是访问次数、平均访问页数、平均访问时长、跳出率、用户画像等无数据。

- A：访问次数、平均访问页数、平均访问时长、跳出率以及用户画像相关的数据统计与用户会话有关，需要开启会话或集成会话相关插件。 [会话集成参考](/tutorials/notes.md#_11会话的集成)
<br><br>

> Q：访客数和用户数是什么关系？

- A：用户数是访客数的去重。在统计周期内，访客总数将周期内的每一天的访客数累计计算，用户数不累计。
 <br><br>  

> Q: 地域分析流量概览有数据但是分布图没有数据的原因是什么？

- A：请求IP需要是外网IP，才能解析地域。
<br><br>

> Q: IP解析的城市信息不准确的原因是什么？

- A：ClkLog IP库采用的是国外免费的ip2location-lite库，在解析过程中可能出现地域解析不准确的情况，如果对地域的准确度有较高的需求，可以自行二开更换更为准确的IP库。
<br><br>

> Q: 地域分布图可以切换为全球的吗？

- 社区版：不支持，可自行二开。付费版：支持全球数据分析。
<br><br>

> Q：访问分析-受访页面-受访页面分析处已有相关数据展示，但是结构化页面分析无数据。

- A：web项目结构化页面数据的统计展示，需要在【系统设置】-【项目管理】处设置项目对外访问的所有域名。
<br><br>

> Q：受访页面中显示的页面URL和实际的页面标题不一致。

- A：该问题通常出现于单页面应用，[原因参考](/tutorials/statindicator.md#_2特有统计指标)
<br><br>

> Q：站外搜索为什么没有数据？

- A：[原因参考](/tutorials/statindicator.md#_23站外搜索词)
<br><br>

> Q：站内搜索为什么没有数据？

- A：[原因参考](/tutorials/statindicator.md#_24站内搜索词)
<br><br>

## 自定义事件分析相关

> Q：社区版支持自定义事件埋点数据的采集吗?

- A：社区版不支持，可自行二开或购买ClkLog付费版。
 <br><br>

> Q：事件分析中每个事件属性都要自己加吗？

- A：自定义事件是根据业务实际情况进行设计定义的，可以通过模板定义好直接一次性导入。
  注意：ClkLog有提供预置事件和属性（预置事件、通用预置属性以及用户预置属性）的模板，在做自定义事件埋点之前，建议先把预置事件和属性相关模板下载下来导入进去，否则自定义事件相关的分析功能无法选择对预置事件及其相关属性进行统计分析。
<br><br>

> Q：事件分析-自定义分析，事件分析指标筛时，为什么在事件指标中看不到预置事件？

- A：预置事件需要先在【元数据管理-元事件】处下载预置事件模板并导入元事件库，才能在自定义分析的事件指标中进行事件筛选。
 <br><br>

<!-- > Q：

- A：
 <br><br>  

> Q：

- A：
 <br><br>  

 > Q：

- A：
 <br><br>   -->