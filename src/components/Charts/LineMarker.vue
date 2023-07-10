<template>
  <div :id="id" :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import echarts from "echarts";
import resize from "./mixins/resize";

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: "chart",
    },
    id: {
      type: String,
      default: "chart",
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
    };
  },
  mounted() {
    this.initChart();
  },
  beforeDestroy() {
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
    this.chart = null;
  },
  methods: {
    initChart() {
      this.chart = echarts.init(document.getElementById(this.id));

      this.chart.setOption({
        backgroundColor: "#FAFAFB",
        // title: {
        //   top: 20,
        //   text: "Requests",
        //   textStyle: {
        //     fontWeight: "normal",
        //     fontSize: 16,
        //     color: "red",
        //   },
        //   left: "1%",
        // },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            lineStyle: {
              color: "#C0CADB",
              width:3,
            },
          },
        },
        legend: {
          top: 20,
          icon: "rect",
          itemWidth: 14,
          itemHeight: 5,
          itemGap: 13,
          data: ["浏览量(PV)", "访问次数"],
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
            data: [0, 4, 8, 12, 16, 20],
            nameTextStyle: {
              fontWeight: 600,
              fontSize: 18,
            },
            axisTick: {
              show: false,
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
            // data: [50000, 100000, 150000, 200000, 250000],
          },
        ],
        series: [
          {
            name: "访问次数",
            type: "line",
            itemStyle: {
              normal: {
                color: "#3D64E6",
                // borderColor: "#5fb4db",
                // borderWidth: 12,
              },
            },
            lineStyle: {
              width: 3,
            },
            data: [152256, 168925, 120322, 45262, 150698, 125550,],
          },
          {
            name: "浏览量(PV)",
            type: "line",
            itemStyle: {
              normal: {
                color: "#A4C4FE",
                // borderColor: "rgba(0,136,212,0.2)",
                // borderWidth: 12,
              },
            },
            lineStyle: {
              // color: "#1791FF",
              width: 3,
            },
            data: [120152, 132120, 265235, 144225, 178212, 168885],
          },
        ],
      });
    },
  },
};
</script>
