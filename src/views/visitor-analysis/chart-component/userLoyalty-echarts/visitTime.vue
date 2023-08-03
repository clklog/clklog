<template>
  <div class="bar_chart setSpace public-hoverItem">
    <div class="public-visited-echarts">
      访问时长
    </div>
    <div id="timeChart" :style="myChartStyle"></div>
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
      xData: [], //横坐标
      yData: [], //人数数据
      myChartStyle: { width: "100%", height: "300px" }, //图表样式
    };
  },
  mounted() {
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
    getVisitTime(val) {
      this.xData = [];
      this.yData = [];
      val.map((item) => {
        if (item) {
          this.xData.push(item.key);
          this.yData.push(item.value);
        }
      });
      this.initEcharts();
    },
    initEcharts() {
      // 多列柱状图
      const visitTimeData = {
        xAxis: {
          data: this.xData,
          axisTick: {
            show: false,
            alignWithLabel: true,
          },
          axisLabel: {
            show: true,
            interval: 0,
            // rotate:45,
          },
        },

        dataZoom: [
          {
            type: "inside",
            startValue: 0,
            endValue: 10,
            minValueSpan: 10,
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
            barWidth: 30,
            name: "浏览量（PV）",
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

      const timeChart = echarts.init(document.getElementById("timeChart"));
      timeChart.setOption(visitTimeData);

      window.addEventListener("resize", () => {
        timeChart.resize();
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
  .setSpace {
    margin-top: 15px;
  }
}
</style>
