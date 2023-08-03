<template>
  <div class="bar_chart setSpace public-hoverItem">
    <div class="public-visited-echarts">
      访问页数
    </div>
    <div id="pageChart" :style="myChartStyle"></div>
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
    getVisitPage(val) {
      console.log(val, "页数");
      this.xData = [];
      this.yData = [];
      val.map((item) => {
        if (item) {
          this.xData.push(item.key);
          this.yData.push(item.value);
        }
      });
      console.log(this.yData, "y轴");
      this.initEcharts();
    },
    initEcharts() {
      const visitPageData = {
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
            zoomOnMouseWheel: false, // 关闭滚轮缩放
            moveOnMouseWheel: true, // 开启滚轮平移
            moveOnMouseMove: true, // 鼠标移动能触发数据窗口平移
          },
        ],
        yAxis: {},
        series: [
          {
            type: "bar", //形状为柱状图
            data: this.yData,
            barWidth: 30, //改变柱子的宽度
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
        ],
      };

      const pageChart = echarts.init(document.getElementById("pageChart"));
      pageChart.setOption(visitPageData);

      window.addEventListener("resize", () => {
        pageChart.resize();
      });
    },
    beforeDestroy() {
      if (!this.chart) {
        return;
      }
      this.chart.dispose();
      this.chart = null;
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
