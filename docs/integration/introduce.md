
ClkLog数据采集是基于神策分析SDK来实现的，在实际应用过程中，首先要完成ClkLog相关应用和服务的安装与部署。在完成ClkLog的安装部署后，采集端应用通过神策分析官方提供的客户端SDK和服务端SDK对数据进行采集，将采集到的数据接收地址配置为ClkLog接收服务【clklog-receiver】的地址，经由ClkLog处理服务【clklog-processing】处理后，在ClkLog前端项目【clklog-ui】去调用统计接口【clklog-api】来实现埋点采集数据的统计分析数据图形化界面展示。

ClkLog数据采集基于神策分析SDK来实现，所以关于如何采用神策分析SDK进行客户端和服务端埋点的集成方式请参考神策分析官方文档。

<!-- 为方便您的快速集成，在该文档我们整理了神策分析常规的SDK的下载和集成文件入口，以供参考，详细参考后续章节“<a href="/#/installation/integration.md#_3埋点集成sdk及文档">**3.埋点集成SDK及文档**</a>”内容。 -->
