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
      oldPercent: 0,
      newPercent: 0,
    };
  },
  mounted() {
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
      console.log(val,"图表数据==》");
      const oldCount = (val && (val.oldUvCount ?? val.oldUv)) ? (val.oldUvCount ?? val.oldUv) : 0;
      const newCount = (val && (val.newUvCount ?? val.newUv)) ? (val.newUvCount ?? val.newUv) : 0;

      // 直接使用父组件传递的百分比字符串数据
      this.oldPercent = (val && (val.oldVistorPercent ?? val.oldPercent)) || "0.00";
      this.newPercent = (val && (val.newVistorPercent ?? val.newPercent)) || "0.00";

      // 当两者皆为0时，展示暂无数据
      if (oldCount === 0 && newCount === 0) {
        if (this.chart) {
          this.chart.dispose();
          this.chart = null;
        }
        this.chart = echarts.init(document.getElementById(this.id));
        this.chart.clear();
        this.chart.setOption({
          title: {
            text: "",
            left: "center",
            top: "center",
            textStyle: { color: "#999", fontSize: 14, fontWeight: "normal" },
          },
          series: [],
          legend: {},
          tooltip: { show: false },
        }, { notMerge: true });
        return;
      }

      this.oldValue = oldCount;
      this.newValue = newCount;
      this.initChart();
    },
    initChart() {
      if (this.chart) {
        this.chart.dispose();
        this.chart = null;
      }
      this.chart = echarts.init(document.getElementById(this.id));
      
      // 只显示有数据的项
      const filteredData = [];
      if (this.newValue > 0) {
        filteredData.push({ value: this.newValue, name: "新访客" });
      }
      if (this.oldValue > 0) {
        filteredData.push({ value: this.oldValue, name: "老访客" });
      }
      
      // 如果没有数据，显示提示
      if (filteredData.length === 0) {
        this.chart.setOption({
          title: {
            text: "暂无数据",
            left: "center",
            top: "center",
            textStyle: { color: "#999", fontSize: 14, fontWeight: "normal" },
          },
          series: [],
          legend: {},
          tooltip: { show: false },
        }, { notMerge: true });
        return;
      }
      
      this.chart.setOption({
        title: {
          left: "center",
          top: "center",
        },
        tooltip: {
          trigger: "item",
          formatter: function(params) {
            // 直接使用字符串格式的百分比
            const percent = params.name === "新访客" ? this.newPercent : this.oldPercent;
            return `${params.name}: ${params.value} (${percent})`;
          }.bind(this),
        },
        legend: {
          orient: "vertical",
          x: "right",
          y: "bottom",
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
              show: true,
            },
            emphasis: {
              label: {
                show: true,
                fontSize: "28",
                fontWeight: "bold",
                formatter: function(params){
                  return params.name;
                }
              },
            },
            itemStyle: {
              normal: {
                color: function (colors) {
                  // 根据数据项名称返回对应颜色
                  if (colors.name === "新访客") {
                    return "#2c7be5"; // 蓝色
                  } else if (colors.name === "老访客") {
                    return "#FFA500"; // 橙黄色 (更明显)
                  }
                  // 默认颜色
                  var colorList = ["#2c7be5", "rgba(253,126,20,0.8)"];
                  return colorList[colors.dataIndex];
                },
              },
            },
            data: filteredData,
          },
        ],
      }, { notMerge: true });
    },
  },
};
</script>
