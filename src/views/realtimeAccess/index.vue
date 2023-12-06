<template>
  <div class="wrapItem">
    <FilterBar
      :ByCalendar="false"
      :ByTime="false"
      @setFilterBarParams="setFilterBarParams"
    ></FilterBar>
    <div class="public-block" style="margin-top: 0">
      <div class="blockDiv setSpace">
        <div class="block-countLine blockLeft public-hoverItem block-main">
          <div class="block-head">
            <div class="block-title">访问量</div>
            <div class="block-head-icon">
              <div class="personTh">0人</div>
              <div class="timeTd">(近半小时)</div>
            </div>
          </div>
          <div id="countLine" style="width: 100%; height: 400px"></div>
        </div>

        <div class="block-countLine blockRight public-hoverItem block-main">
          <div class="block-head">
            <div class="block-title">热门网页</div>
            <div class="block-head-icon"></div>
          </div>
          <div style="width: 100%">
            <el-table
              :data="tableData"
              class="public-radius"
              :header-cell-style="{
                textAlign: 'center',
                background: '#f7fafe ',
              }"
              :cell-style="tableHeaderColor"
              style="width: 100%"
              border
            >
              <el-table-column
                type="index"
                label="排名"
                min-width="15%"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                prop="httpReferrer"
                min-width="55%"
                :show-overflow-tooltip="true"
                label="页面Url"
              >
              </el-table-column>
              <el-table-column
                prop="pv"
                label="访问量"
                min-width="30%"
                :show-overflow-tooltip="true"
              />
            </el-table>
          </div>
        </div>
      </div>

      <div
        class="localMap setSpace public-hoverItem block-countLine block-main"
      >
        <regionalDistribu></regionalDistribu>
      </div>
      <div class="visitLog public-hoverItem block-countLine block-main">
        <visitLog></visitLog>
      </div>
    </div>
  </div>
</template>

<script>
import echarts from "echarts";
import {
  getVisitUriDetailApi,
  getVisitUriTotalApi,
  getVisitUriPathTreeTotalApi,
} from "@/api/trackingapi/visituri";
import { FilterBar } from "@/layout/components";
import { copyObj } from "@/utils/copy";
import { regionalDistribu,visitLog } from "./components/index";

import { percentage } from "@/utils/percent";
export default {
  components: {
    FilterBar,
    regionalDistribu,
    visitLog
  },
  data() {
    return {
      countLineChart: null,
      filterBarParams: {},
      originData: null,
      pageNum: 1,
      pageSize: 10,
      sortName: null,
      sortOrder: null,
      tableData: [
        {
          pv: "2016-05-02",
          httpReferrer: "上海市普陀区金沙江路 1518 弄",
        },
        {
          pv: "2016-05-04",

          httpReferrer: "上海市普陀区金沙江路 1517 弄",
        },
        {
          pv: "2016-05-01",

          httpReferrer: "上海市普陀区金沙江路 1519 弄",
        },
        {
          pv: "2016-05-03",

          httpReferrer: "上海市普陀区金沙江路 1516 弄",
        },
      ],
    };
  },
  computed: {
    projectName() {
      return this.$store.getters.projectName;
    },
    commonParams() {
      const { projectName } = this;
      return Object.assign({ projectName }, this.filterBarParams);
    },
  },
  onmounted() {},
  watch: {
    commonParams() {
      this.countLineEcharts();
    },
  },
  methods: {
    tableHeaderColor({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 1) {
        return "text-align:left";
      } else {
        return "text-align:center";
      }
    },
    countLineEcharts() {
      this.countLineChart = echarts.init(document.getElementById("countLine"));
      var option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            lineStyle: {
              color: "#C0CADB",
              width: 2,
            },
          },
        },
        legend: {
          x: "center",
          y: "top",
        },
        grid: {
          left: "13%",
          right: "14%",
          top: "25%",
          bottom: "10%",
          containLabel: true,
        },

        dataZoom: [
          {
            type: "inside",
            startValue: 0,
            endValue: 24,
            minValueSpan: 10,
            zoomOnMouseWheel: false, // 关闭滚轮缩放
            moveOnMouseWheel: true, // 开启滚轮平移
            moveOnMouseMove: true, // 鼠标移动能触发数据窗口平移
          },
        ],

        xAxis: {
          type: "category",
          data: ["A", "B", "C"],
          axisTick: {
            show: false,
          },
          axisLabel: {
            interval: 0,
            // rotate: "45",
          },
        },
        yAxis: {
          axisTick: {
            show: false,
          },
          type: "value",
        },
        series: [
          {
            name: "0人(近半小时)",
            symbol: "circle",
            data: [120, 200, 150],
            type: "line",
            itemStyle: {
              normal: {
                color: " #2c7be5",
              },
            },
          },
        ],
      };
      this.countLineChart.setOption(option);
      window.addEventListener("resize", this.handleSize);
    },
    handleSize() {
      this.countLineChart && this.countLineChart.resize();
    },

    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
    currentPage(val) {
      this.getVisitUriDetail(val);
    },
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.handleSize);
  },
};
</script>

<style lang="scss" scoped>
.wrapItem {
  .personTh {
    width: 58px;
    height: 30px;
    background: rgba(255, 255, 255, 0.39);
    border: 1px solid #dee0ec;
    border-radius: 6px 0px 0px 6px;
    line-height: 30px;
    text-align: center;
    font-size: 16px;
    font-weight: bold;
    color: #2c7be5;
  }
  .timeTd {
    width: 99px;
    height: 30px;
    background: rgba(255, 255, 255, 0.39);
    border: 1px solid #dee0ec;
    border-radius: 0px 6px 6px 0px;
    line-height: 30px;
    text-align: center;
    font-size: 13px;
    font-weight: 400;
    color: #4d4d4d;
  }
  .innerFont {
    font-size: 13px;
    font-weight: 400;
    color: #4d4d4d;
    margin-left: 8px;
  }
  .setSpace {
    margin-bottom: 20px;
  }
  .blockDiv {
    box-sizing: border-box;
    min-height: 300px;
    display: flex;
    justify-content: space-between;
    .blockLeft {
      width: calc(50% - 10px);
      background-color: #fff;
      min-height: 502px;
      border-radius: 6px;
    }
    .blockRight {
      width: calc(50% - 10px);
      background-color: #fff;
      min-height: 502px;
      border-radius: 6px;
    }
  }
  .localMap {
    height: 514px;
    background-color: #fff;
    border-radius: 6px;
  }
  .visitLog {
    height: 585px;
    background-color: #fff;
    border-radius: 6px;
  }
}
</style>
