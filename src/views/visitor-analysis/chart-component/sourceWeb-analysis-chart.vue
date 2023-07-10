<template>
  <div>
    <div class="chartsIcon">
      <div class="chartLeft">
        <div class="trendHead">
          <div class="trafficHead">TOP10来源网站指标展示</div>
          <div class="block">
            <el-cascader
              v-model="emptyList"
              style="width: 170px"
              placeholder="指标 | 选项"
              :options="options"
              :props="{ multiple: true, checkStrictly: true }"
              clearable
              collapse-tags
            />
          </div>
        </div>
        <div class="echart" id="mychart" :style="myChartStyle"></div>
      </div>
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
      xData: ["百度", "腾讯", "阿里", "哔哩哔哩", "至存", "国拍", "机动车"], //横坐标
      yData: [23, 24, 18, 25, 27, 28, 25], //人数数据
      taskDate: [10, 11, 9, 17, 14, 13, 14],
      myChartStyle: { float: "left", width: "100%", height: "400px" }, //图表样式
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
        legend: {
          data: ["浏览量（PV）", "访问次数"],
          top: "0%",
        },
        yAxis: {},
        series: [
          {
            type: "bar", //形状为柱状图
            data: this.yData,
            name: "浏览量（PV）", // legend属性
            label: {
              // 柱状图上方文本标签，默认展示数值信息
              show: true,
              position: "top",
            },
            itemStyle:{
                color:'#051e71',
            }
            
          },
          {
            type: "bar", //形状为柱状图
            data: this.taskDate,
            name: "访问次数", // legend属性
            label: {
              // 柱状图上方文本标签，默认展示数值信息
              show: true,
              position: "top",
            },
            itemStyle:{
                color:'#3d64e6',
            }
          },
        ],
      };
      const myChart = echarts.init(document.getElementById("mychart"));
      myChart.setOption(mulColumnZZTData);
      //随着屏幕大小调节图表
      window.addEventListener("resize", () => {
        myChart.resize();
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.trafficHead {
  font-size: 16px;
  font-weight: 400;
  padding: 20px 30px 20px;
}
img {
  width: 11.44px;
  height: 11.44px;
  margin-left: 12px;
  cursor: pointer;
}
.trendHead {
  display: flex;
  align-items: center;
  position: relative;
  .iconFlag {
    position: absolute;
    right: 13px;
    top: 19px;
    color: #acaaaa;
    font-size: 16px;
    cursor: pointer;
  }
}
.chartsIcon {
  margin: 20px;
  width: calc(100% -20px);
  min-height: 300px;
  display: flex;
  justify-content: space-between;
  .chartLeft {
    background-color: #fafafb;
    padding: 15px;
    width: 100%;
    min-height: 400px;
    .point {
      padding-right: 10px;
    }
  }
}
</style>
