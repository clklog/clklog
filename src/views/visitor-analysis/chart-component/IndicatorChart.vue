<template>
  <div>
    <div class="chartsIcon">
      <div class="chartLeft">
        <div class="trendHead">
          <div class="trafficHead">指示分析图</div>
          <div class="block">
            <!-- <el-cascader
              v-model="pointValue"
              style="width: 280px;"
              placeholder="指标 | 选项"
              :options="options"
              :props="{ multiple: true, checkStrictly: true }"
              clearable
              @change="changeChartValue"
            /> -->
            <el-select
              class="custom_select"
              v-model="headLege"
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
  props: ["flowTrendListed"],
  data() {
    return {
      disabledSelect: [],
      timeTypeValue: "day",
      chart: null,
      pointValue: ["浏览量", "访问次数"],
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
      pv: [],
      uv: [],
      visitCount: [],
      ipCount: [],
      bounceRate: [],
      time: [],
      headLege: [],
      flowTrendList: [],
    };
  },
  watch: {
    flowTrendList(val) {
      if (val && val.length > 0) {
        this.checkSearchValue(this.pointValue);
      } else {
        this.checkSearchValue([]);
      }
    },
  },
  mounted() {
    this.headLege = this.pointValue;
    this.initChart();
    return;
  },
  beforeDestroy() {
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
    this.chart = null;
  },
  methods: {
    // 传递数据内容
    apiDetailList(val) {
      this.flowTrendList = val.detail;
    },
    changeChartValue(e) {
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
      this.pointValue = this.headLege = e.flat(Infinity);
      this.checkSearchValue(this.headLege);
    },
    checkSearchValue(val) {
      this.time = [];
      this.pv = [];
      this.uv = [];
      this.visitCount = [];
      this.ipCount = [];
      this.bounceRate = [];
      const { time, pv, uv, visitCount, ipCount, bounceRate } = this;
      this.flowTrendList.map((item) => {
        if (item.statTime) {
          return time.push(item.statTime);
        } else {
          time.push(0);
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
      this.chart = echarts.init(document.getElementById("indicator"));
      this.chart.setOption({
        backgroundColor: "#FAFAFB",
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
            data: this.time,
            axisLabel: { interval: 1 },
            nameTextStyle: {
              fontWeight: 600,
              fontSize: 18,
            },
            axisTick: {
              show: false,
            },
            axisLabel: {
              interval: 0, //强制文字产生间隔
              rotate: "45", //旋转角度
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
                color: "#A4C4FE",
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
                color: "#007EE9",
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
                color: "#3D64E6",
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
                color: "#00338C",
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
                color: "#17DDFF",
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
<style lang="scss" scoped>
@import "~@/styles/components/custom-select.scss";
::v-deep {
  @import "~@/styles/components/el-checkbox.scss";
}
.trafficHead {
  font-size: 14px;
  font-weight: 500;
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
