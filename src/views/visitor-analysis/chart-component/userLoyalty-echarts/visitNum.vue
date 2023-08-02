<template>
  <div class="bar_chart">
    <div style="padding-left: 9px; padding-top: 40px; display: flex">
      <span>访问次数</span>
    </div>
    <div id="numChart" :style="myChartStyle"></div>
  </div>
</template>

<script>
import echarts from "echarts";
export default {
  data() {
    return {
      chart: null,
      emptyList: "",
     
      xData: [],
      yData: [],
      myChartStyle: { width: "100%", height: "300px" }, //图表样式
    };
  },
  methods: {
    getVisitNum(val) {
      console.log(val,"次数");
      this.xData = []
      this.yData = []
      val.map((item) => {
        if (item) {
          this.xData.push(item.key);
          this.yData.push(item.value);
        }
      });
      this.initEcharts();
    },

    initEcharts() {
      const mulColumnZZTData = {
        xAxis: {
          data: this.xData,
          axisTick: {
            show: false,
            alignWithLabel: true,
          },
        },
       
        dataZoom: [
          // {
          //   type: "slider",
          //   show: true,
          //   start: 1,
          //   end: 100, // 改变柱子之间的间隔
          //   handleSize: 3,
          // },
          {
            type: "inside",
            startValue: 0,
            endValue: 16,
            // minValueSpan: 10,
            // yAxisIndex: [0],
            zoomOnMouseWheel: false, // 关闭滚轮缩放
            moveOnMouseWheel: true, // 开启滚轮平移
            moveOnMouseMove: true, // 鼠标移动能触发数据窗口平移
          },
        ],
        yAxis: {},
        series: [
          {
            type: "bar",
            data: this.yData,
            barWidth: 30, //改变柱子的宽度
            name: "浏览量（PV）", // legend属性
            label: {
              show: true,
              position: "top",
            },
            itemStyle: {
              color: "#3D64E6",
            },
          },
        ],
      };
      const numChart = echarts.init(document.getElementById("numChart"));
      numChart.setOption(mulColumnZZTData);
      window.addEventListener("resize", () => {
        numChart.resize();
      });
    },
  },
  beforeDestroy() {
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
    this.chart = null;
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
  .setSpace {
    margin-top: 15px;
  }
}
</style>
