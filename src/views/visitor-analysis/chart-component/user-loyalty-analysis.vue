<template>
  <div class="userLoyalty">
    <span>用户忠诚度分析</span>
    <!-- <div class="chartsIcon">
      <div class="chartLeft">
        <div class="trendHead">
          <div class="trafficHead">TOP10来源网站指标展示</div>
        </div>
        <div class="echart" id="mychart" :style="myChartStyle"></div>
      </div>
    </div> -->
    <div class="bar_chart">
      <div style="padding-left: 9px; padding-top: 40px; display: flex">
        <span>访问页数</span>
      </div>
      <div class="echart" id="mychart" :style="myChartStyle"></div>
    </div>

    <div class="bar_chart setSpace">
      <div style="padding-left: 9px; padding-top: 40px; display: flex">
        <span>访问频次</span>
      </div>
      <div class="echart" id="mychart2" :style="myChartStyle"></div>
    </div>
    <div class="bar_chart setSpace">
      <div style="padding-left: 9px; padding-top: 40px; display: flex">
        <span>访问时长</span>
      </div>
      <div class="echart" id="mychart3" :style="myChartStyle"></div>
    </div>
  </div>
</template>

<script>
import echarts from "echarts";
export default {
  data() {
    return {
      chart: null,
      emptyList: "",
      options: [
        {
          value: 1,
          label: "浏览量",
        },
        {
          value: 2,
          label: "访客数",
        },
        {
          value: 3,
          label: "访问次数",
        },
        {
          value: 4,
          label: "IP数",
        },
        {
          value: 5,
          label: "跳出率",
        },
      ],
      xData: ["百度", "腾讯", "阿里", "哔哩哔哩", "至存", "国拍", "机动车",'213',"fjads"], //横坐标
      yData: [23, 24, 18, 25, 27, 28, 25,32,321], //人数数据
      taskDate: [10, 11, 9, 17, 14, 13, 14],
      myChartStyle: {  width: "100%", height: "300px" }, //图表样式
    };
  },
  mounted() {
    // this.initChart();
    this.initEcharts();
  },
  beforeDestroy() {
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
    this.chart = null;
  },
  methods: {
    initEcharts() {
      // 多列柱状图
      const mulColumnZZTData = {
        xAxis: {
          data: this.xData,
        },
        // 图例
        // legend: {
        // //   data: ["浏览量（PV）", "访问次数"],
        //   top: "0%",
        //   left:"0%"
        // },
        dataZoom: [
        //   {
        //     type: 'slider',
        //     show: true,
        //     start: 1,
        //     end: 3,     // 改变柱子之间的间隔
        //     handleSize: 5
        //   },
        ],
        yAxis: {},
        series: [
          {
            type: "bar", //形状为柱状图
            data: this.yData,
            barWidth: 30,     //改变柱子的宽度
            name: "浏览量（PV）", // legend属性
            label: {
              // 柱状图上方文本标签，默认展示数值信息
              show: true,
              position: "top",
            },
            itemStyle: {
              color: "#3D64E6",
            },
          },
        //   {
        //     type: "bar", //形状为柱状图
        //     data: this.taskDate,
        //     name: "访问次数", // legend属性
        //     label: {
        //       // 柱状图上方文本标签，默认展示数值信息
        //       show: true,
        //       position: "top",
        //     },
        //     itemStyle: {
        //       color: "#3d64e6",
        //     },
        //   },
        ],
      };
      const myChart = echarts.init(document.getElementById("mychart"));
      myChart.setOption(mulColumnZZTData);
      //随着屏幕大小调节图表

    //   多个echarts
    const myChart2 = echarts.init(document.getElementById("mychart2"));
      myChart2.setOption(mulColumnZZTData);
      const myChart3 = echarts.init(document.getElementById("mychart3"));
      myChart3.setOption(mulColumnZZTData);

      window.addEventListener("resize", () => {
        myChart.resize();
        myChart2.resize();
        myChart3.resize();
    
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.userLoyalty {
  height: 1109px;
  margin: 15px;
  background-color: #fafafb;
  padding-top: 18px;
  padding-left: 16px;
  padding-bottom: 30px;
  span {
    font-size: 14px;
    font-weight: 500;
    line-height: 33px;
    color: #4d4d4d;
  }
  .bar_chart {
    height: 337px;
    background: #fff;
    span {
      font-size: 12px;
      line-height: 15px;
      color: #656d92;
    }
    
  }
  .setSpace{
        margin-top: 15px;
    }
}
</style>
