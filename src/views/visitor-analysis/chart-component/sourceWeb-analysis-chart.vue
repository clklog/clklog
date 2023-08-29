<template>
  <div>
    <div class="chartsIcon">
      <div class="chartLeft public-hoverItem">
        <div class="trendHead">
          <div class="trafficHead">TOP10来源网站指标展示</div>
          <div class="block">
            <el-select
              class="custom_select"
              v-model="pointValue"
              multiple
              placeholder="请选择"
              style="min-width: 280px"
              @change="changeChartValue"
            >
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
                :disabled="disabledSelect.includes(item.value)"
              >
                <div style="display: flex; align-items: center">
                  <span class="checkbox__inner"
                    ><div class="inner-box"></div
                  ></span>
                  <span style="">{{ item.label }}</span>
                </div>
              </el-option>
            </el-select>
          </div>
        </div>
        <div id="mychart" :style="myChartStyle"></div>
      </div>
    </div>
  </div>
</template>

<script>
import echarts from "echarts";
export default {
  data() {
    return {
      sourceChartList: [],
      chart: null,
      emptyList: "",
      options: [
        {
          value: "浏览量",
          label: "浏览量",
        },
        {
          value: "访客数",
          label: "访客数",
        },
        {
          value: "访问次数",
          label: "访问次数",
        },
        {
          value: "IP数",
          label: "IP数",
        },
        {
          value: "跳出率",
          label: "跳出率",
        },
      ],
      xData: [], //website
      myChartStyle: { float: "left", width: "100%", height: "400px" },
      pv: [],
      uv: [],
      visitCount: [],
      ipCount: [],
      bounceRate: [],
      time: [],
      headLege: [],
      pointValue: ["浏览量", "访客数"],
      disabledSelect: [],
      seriesdata:[],
    };
  },
  mounted() {},
  methods: {
    getSourceChart(val) {
      this.pv = [];
      this.uv = [];
      this.visitCount = [];
      this.ipCount = [];
      this.bounceRate = [];
      this.xData = [];
      // this.sourceChartList = val.detail;
      this.sourceChartList = val;
      this.sourceChartList.map((item) => {
        if (item.sourcesite) {
          let hostName = this.getHost(item.sourcesite);
          this.xData.push(hostName);
        } else {
          this.xData.push(0);
        }
      });
      this.filterData(this.pointValue);
    },
    getHost(url) {
      let host = "";
      let regex = /^\w+\:\/\/([^\/]*).*/;
      let match = url.match(regex);
      if (match) {
        host = match[1];
      } else {
        host = url;
      }
      return host;
    },
    changeChartValue(e) {
      this.pv = [];
      this.uv = [];
      this.visitCount = [];
      this.ipCount = [];
      this.bounceRate = [];
      let result = [];
      for (let i = 0; i < this.options.length; i++) {
        result.push(this.options[i].label);
        if (e.length > 2) {
          this.disabledSelect = result.filter((item) => {
            return e.indexOf(item) == -1;
          });
        } else {
          this.disabledSelect = [];
        }
      }
      this.filterData(this.pointValue);
    },
    filterData(val) {
      const { pv, uv, visitCount, ipCount, bounceRate } = this;
      if (val.includes("浏览量")) {
        this.sourceChartList.map((item) => {
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
        this.sourceChartList.map((item) => {
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
        this.sourceChartList.map((item) => {
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
        this.sourceChartList.map((item) => {
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
        this.sourceChartList.map((item) => {
          if (item.bounceRate) {
            return bounceRate.push(item.bounceRate);
          } else {
            bounceRate.push(0);
          }
        });
      } else {
        this.bounceRate = [];
      }

      let seriesdata = [
        {
          type: "bar",
          data: this.pv,
          barWidth: 20,
          barGap: "100%", 
          name: "浏览量",
          label: {
            show: true,
            position: "top",
          },
          itemStyle: {
            color: "#d5e5fa",
          },
        },
        {
          type: "bar",
          data: this.uv,
          barWidth: 20,
          name: "访客数",
          label: {
            show: true,
            position: "top",
          },
          itemStyle: {
            color: "#80b0ef",
          },
        },
        {
          type: "bar",
          data: this.visitCount,
          barWidth: 20,
          name: "访问次数",
          label: {
            show: true,
            position: "top",
          },
          itemStyle: {
            color: "#5695ea",
          },
        },
        {
          type: "bar",
          data: this.ipCount,
          barWidth: 20,
          name: "IP数",
          label: {
            show: true,
            position: "top",
          },
          itemStyle: {
            color: "2c7be5",
          },
        },
        {
          type: "bar",
          data: this.bounceRate,
          barWidth: 20,
          name: "跳出率",
          label: {
            show: true,
            position: "top",
          },
          itemStyle: {
            color: "#1a4a89",
          },
        },
      ];
      for (let i = 0; i < seriesdata.length; i++) {
        if (!this.pointValue.includes(seriesdata[i].name)) {
          seriesdata.splice(i--, 1);
        }
      }
      this.initEcharts(seriesdata);
    },
    initEcharts(val) {
       const sourceChart = {
        tooltip: {
          trigger: "axis",
          showContent: true,
          formatter: function (params) {
            var res = "<div><p>" + params[0].name + "</p></div>";
            for (var i = 0; i < params.length; i++) {
              res +=
                "<p>" + params[i].seriesName + ":" + params[i].value + "</p>";
            }
            return res;
          },
        },
        xAxis: {
          type: "category",
          boundaryGap: true,
          data: this.xData,
          axisLabel: {
            interval: 0, //强制文字产生间隔
            rotate: "30", //旋转角度
          },
          axisLine: {
            onZero: false,
            // lineStyle: {
            //   color: "#D9D9D9",
            // },
          },
          axisTick: {
            show: false,
            alignWithLabel: true,
          },
        },
        grid: {
          // left: "30%",
          bottom: "22%",
        },
        legend: {
          data: this.pointValue,
          top: "0%",
        },
        dataZoom: [
          // {
          //   type: "slider",
          //   show: true,
          //   startValue: 0,
          //   endValue: 5,
          //   // start: 1,
          //   // end: 3, // 改变柱子之间的间隔
          //   // handleSize: 10,
          // },
          {
            type: "inside",
            startValue: 0,
            endValue: 4,
            // minValueSpan: 10,
            zoomOnMouseWheel: false, // 关闭滚轮缩放
            moveOnMouseWheel: true, // 开启滚轮平移
            moveOnMouseMove: true, // 鼠标移动能触发数据窗口平移
          },
        ],
        yAxis: {
          // onZero: false,
        },
        series: val,
      };
      const myChart = echarts.init(document.getElementById("mychart"));
      myChart.setOption(sourceChart,true);
      window.addEventListener("resize", () => {
        myChart.resize();
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
@import "~@/styles/components/custom-select.scss";
.trafficHead {
  font-size: 16px;
  font-weight: 600;
  color: #4d4d4d;
  padding: 15px 13px 18px 0px;
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
  // width: calc(100% -20px);
  box-sizing: border-box;
  min-height: 300px;
  display: flex;
  justify-content: space-between;
  .chartLeft {
    background-color: #fff;
    padding: 15px;
    width: 100%;
    min-height: 400px;
    .point {
      padding-right: 10px;
    }
  }
}
</style>
