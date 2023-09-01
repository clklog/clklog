<template>
  <div :id="id" :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import echarts from "echarts";
import resize from "./mixins/resize";

export default {
  mixins: [resize],
  props: {
    defaultLege: {},
    flowTrendListed: {},
    className: {
      type: String,
      default: "chart",
    },
    id: {
      type: String,
      default: "id",
    },
    width: {
      type: String,
      default: "200px",
    },
    height: {
      type: String,
      default: "200px",
    },
  },
  data() {
    return {
      chart: null,
      pv: [],
      uv: [],
      visitCount: [],
      ipCount: [],
      bounceRate: [],
      statTime: [],
      headLege: [],
      flowTrendList: [],
    };
  },
  mounted() {
    this.initChart();
    this.flowTrendList = this.flowTrendListed;
    this.headLege = this.defaultLege;
  },
  beforeDestroy() {
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
    this.chart = null;
  },
  methods: {
    getApiTrendList(val, headLege) {
      this.flowTrendList = val;
      this.checkSearchValue(headLege);
    },
    changeLegend(val) {
      this.headLege = val;
      this.checkSearchValue(val);
    },
    checkSearchValue(val) {
      this.statTime = [];
      this.pv = [];
      this.uv = [];
      this.visitCount = [];
      this.ipCount = [];
      this.bounceRate = [];
      const { statTime, pv, uv, visitCount, ipCount, bounceRate } = this;

      this.flowTrendList.map((item) => {
        if (item.statTime) {
          item.statTime = item.statTime.replace(/2023-/g, "");
          if (item.statTime.length <= 2) {
            item.statTime = item.statTime + "时"
          }
          return statTime.push(item.statTime);
        } else {
          statTime.push(0);
        }
      });
      if (val.includes("浏览量")) {
        this.flowTrendList.map((item) => {
          if (item.pv) {
            return pv.push(item.pv);
          } else {
            pv.push(0);
          }
        });
      } else {
        this.pv = [];
      }
      if (val.includes("访客数")) {
        this.flowTrendList.map((item) => {
          if (item.uv) {
            return uv.push(item.uv);
          } else {
            uv.push(0);
          }
        });
      } else {
        this.uv = [];
      }
      if (val.includes("IP数")) {
        this.flowTrendList.map((item) => {
          if (item.ipCount) {
            return ipCount.push(item.ipCount);
          } else {
            ipCount.push(0);
          }
        });
      } else {
        this.ipCount = [];
      }
      if (val.includes("访问次数")) {
        this.flowTrendList.map((item) => {
          if (item.visitCount) {
            return visitCount.push(item.visitCount);
          } else {
            visitCount.push(0);
          }
        });
      } else {
        this.visitCount = [];
      }

      if (val.includes("跳出率")) {
        this.flowTrendList.map((item) => {
          if (item.bounceRate) {
            return bounceRate.push(item.bounceRate);
          } else {
            bounceRate.push(0);
          }
        });
      } else {
        this.bounceRate = [];
      }
      this.initChart();
    },
    initChart() {
      this.chart = echarts.init(document.getElementById(this.id));

      this.chart.setOption({
        backgroundColor: "#fff",
        tooltip: {
          trigger: "axis",
          axisPointer: {
            lineStyle: {
              color: "#C0CADB",
              width: 3,
            },
          },
        },
        legend: {
          top: 20,
          icon: "rect",
          itemWidth: 14,
          itemHeight: 5,
          itemGap: 13,
          data: this.headLege,
          right: "4%",
          textStyle: {
            fontSize: 12,
            color: "black",
          },
        },
        grid: {
          top: 100,
          left: "2%",
          right: "2%",
          bottom: "2%",
          containLabel: true,
        },
        xAxis: [
          {
            type: "category",
            boundaryGap: false,
            data: this.statTime,
            axisLabel: { interval: 1 },
            nameTextStyle: {
              fontWeight: 600,
              fontSize: 18,
            },
            axisTick: {
              show: false,
            },
            axisLabel: {
              interval: 0, 
              rotate: "45", 
            },
          },
        ],
        yAxis: [
          {
            type: "value",
            nameTextStyle: {
              fontWeight: 600,
              fontSize: 18,
            },
            axisTick: {
              show: false,
            },
          },
        ],
        series: [
          {
            name: "浏览量",
            type: "line",
            itemStyle: {
              normal: {
                color: "#2c7be5",
              },
            },
            lineStyle: {
              width: 3,
            },
            data: this.pv,
          },
          {
            name: "访客数",
            type: "line",
            itemStyle: {
              normal: {
                color: "#4c8fe9",
              },
            },
            lineStyle: {
              width: 3,
            },
            data: this.uv,
          },
          {
            name: "访问次数",
            type: "line",
            itemStyle: {
              normal: {
                color: "#6ba3ed",
                // borderColor: "#5fb4db",
                // borderWidth: 12,
              },
            },
            lineStyle: {
              width: 3,
            },
            data: this.visitCount,
          },
          {
            name: "IP数",
            type: "line",
            itemStyle: {
              normal: {
                color: "#8bb6f1",
              },
            },
            lineStyle: {
              width: 3,
            },
            data: this.ipCount,
          },
          {
            name: "跳出率",
            type: "line",
            itemStyle: {
              normal: {
                color: "#abcaf5",
              },
            },
            lineStyle: {
              width: 3,
            },
            data: this.bounceRate,
          },
        ],
      });
    },
  },
};
</script>
