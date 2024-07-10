   Clklog Docker Compose包含标准模式和快速模式两种。

   快速模式：表示日志直接存入clickhouse。[快速模式安装](/installation/quickmode.md)

   标准模式：采集日志数据先存入kafka，经由flink处理后再存入clickhouse。
   [标准模式安装](/installation/standard.md)

   安装过程中可以根据实际需求选择采用标准模式还是快速模式，后续过程可以切换模式。

   [标准模式和快速模式架构](/introduce.md?id=系统架构)
