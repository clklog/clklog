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
      oldValue: null,
      newValue: null,
    };
  },
  mounted() {
    // this.initChart();
  },
  beforeDestroy() {
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
    this.chart = null;
  },
  methods: {
    getPercentData(val) {
      if (val.newVistorPercent && val.oldVistorPercent) {
        this.oldValue = val.oldVistorPercent.replace("%", "");
        this.newValue = val.newVistorPercent.replace("%", "");
      } else {
        this.oldValue = "";
        this.newValue = "";
      }
      this.initChart();
    },
    initChart() {
      this.chart = echarts.init(document.getElementById(this.id));
      this.chart.setOption({
        title: {
          // text: "新老访客",
          left: "center",
          top: "center",
        },
        legend: {
          orient: "vertical",
          x: "right",
          y: "bottom",
          data: ["新访客", "老访客"],
        },
        series: [
          {
            type: "pie",
            radius: ["50%", "70%"],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: "center",
            },
            labelLine: {
              show: false,
            },
            emphasis: {
              label: {
                show: true,
                fontSize: "30",
                fontWeight: "bold",
              },
            },
            itemStyle: {
              normal: {
                color: function (colors) {
                  var colorList = ["#3d64e6", "rgb(155, 156, 156)"];
                  return colorList[colors.dataIndex];
                },
              },
            },
            data: [
              // this.oldValue,this.newValue,
              { value: this.newValue, name: "新访客" },
              { value: this.oldValue, name: "老访客" },
            ],
          },
        ],
      });
    },
  },
};
</script>
