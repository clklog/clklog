<template>
  <div>
    <div class="chartsIcon">
      <div class="chartLeft">
        <div class="trendHead">
          <div class="trafficHead">指示分析图</div>
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
        <div id="indicator" style="height: 350px; width: 97%"></div>
      </div>
    </div>
  </div>
</template>

<script>
import echarts from "echarts";
import resize from "@/components/Charts/mixins/resize";
import Chart from "@/components/Charts/LineMarker";
import pieChart from "@/components/Charts/pieMarker.vue";
import Bus from "@/utils/bus";
export default {
  name: "TrendChart",
  components: { Chart, pieChart },
  mixins: [resize],
  props: ["flowTrendList"],
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
    };
  },
  mounted() {
    console.log(this.$props.flowTrendList);
    this.initChart();
    Bus.$on("trendAnalysis", (res) => {
      // console.log(res,"兄弟传参");
    });
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
      this.chart = echarts.init(document.getElementById("indicator"));

      this.chart.setOption({
        backgroundColor: "#FAFAFB",
        title: {
          top: 20,
          text: "Requests",
          textStyle: {
            fontWeight: "normal",
            fontSize: 16,
            color: "#F1F1F3",
          },
          left: "1%",
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            lineStyle: {
              color: "#57617B",
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
            color: "#red",
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
            axisLine: {
              lineStyle: {
                color: "#57617B",
              },
            },
            data: [0, 4, 8, 12, 16, 20],
          },
        ],
        yAxis: [
          {
            type: "value",
            // name: '(%)',
            axisTick: {
              show: false,
            },
            axisLine: {
              lineStyle: {
                color: "#57617B",
              },
            },
            axisLabel: {
              margin: 10,
              textStyle: {
                fontSize: 14,
              },
            },
            splitLine: {
              lineStyle: {
                color: "#57617B",
              },
            },
          },
        ],
        series: [
          {
            name: "访问次数",
            type: "line",
            smooth: true,
            symbol: "circle",
            symbolSize: 5,
            showSymbol: false,
            lineStyle: {
              normal: {
                width: 1,
              },
            },
            areaStyle: {
              normal: {
                color: new echarts.graphic.LinearGradient(
                  0,
                  0,
                  0,
                  1,
                  [
                    {
                      offset: 0,
                      color: "rgba(137, 189, 27, 0.3)",
                    },
                    {
                      offset: 0.8,
                      color: "rgba(137, 189, 27, 0)",
                    },
                  ],
                  false
                ),
                shadowColor: "rgba(0, 0, 0, 0.1)",
                shadowBlur: 10,
              },
            },
            itemStyle: {
              normal: {
                color: "rgb(137,189,27)",
                borderColor: "#5fb4db",
                borderWidth: 12,
              },
            },
            // data: [220, 182, 191, 134, 150, 120, 110, 125, 145, 122, 165, 122]
            data: [1522, 16895, 12032, 4562, 150698, 120],
          },
          {
            name: "浏览量(PV)",
            type: "line",
            smooth: true,
            symbol: "circle",
            symbolSize: 5,
            showSymbol: false,
            lineStyle: {
              normal: {
                width: 1,
              },
            },
            areaStyle: {
              normal: {
                color: new echarts.graphic.LinearGradient(
                  0,
                  0,
                  0,
                  1,
                  [
                    {
                      offset: 0,
                      color: "rgba(0, 136, 212, 0.3)",
                    },
                    {
                      offset: 0.8,
                      color: "rgba(0, 136, 212, 0)",
                    },
                  ],
                  false
                ),
                shadowColor: "rgba(0, 0, 0, 0.1)",
                shadowBlur: 10,
              },
            },
            itemStyle: {
              normal: {
                color: "rgb(0,136,212)",
                borderColor: "rgba(0,136,212,0.2)",
                borderWidth: 12,
              },
            },
            data: [12012, 13210, 26535, 14425, 17822, 168885],
          },
        ],
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
