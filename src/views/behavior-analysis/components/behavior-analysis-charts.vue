<template>
  <div>
    <div class="chartsIcon">
      <div class="chartLeft">
        <div class="trendHead">
          <div class="public-firstHead">指标分析图</div>
          <div class="block">
            <!-- <el-cascader
              v-model="emptyList"
              style="width: 170px"
              placeholder="指标 | 选项"
              :options="options"
              :props="{ multiple: true, checkStrictly: true }"
              clearable
              collapse-tags
            /> -->
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
        <div class="echart" id="mychart" :style="myChartStyle"></div>
      </div>
    </div>

    <div class="search_wrappy">
      <div class="search_table">
        <span class="public-firstHead">用户列表</span>
        <div class="setTable">
          <el-table
            border
            :data="userListData"
            :header-cell-style="{ textAlign: 'center' }"
            :cell-style="{ textAlign: 'center' }"
            style="width: 100%; margin-top: 12px; height: 98%"
          >
            <el-table-column
              :show-overflow-tooltip="true"
              style="cursor: pointer !important"
              prop="distinctId"
              label="访客ID"
              width="200"
            >
              <template slot-scope="scope">
                <div
                  @click="handleCellClick(scope.row.distinctId)"
                  style="
                    cursor: pointer;
                    overflow: hidden;
                    text-overflow: ellipsis;
                  "
                >
                  {{ scope.row.distinctId }}
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="visitorType" label="访客类型" sortable>
            </el-table-column>
            <el-table-column prop="visitCount" label="访客次数" sortable>
            </el-table-column>
            <el-table-column prop="pv" label="浏览量" sortable>
            </el-table-column>
            <el-table-column prop="visitTime" label="停留时长" sortable>
            </el-table-column>
            <el-table-column prop="avgPv" label="平均访问页数" sortable>
            </el-table-column>
            <el-table-column prop="latestTime" label="上次访问时间" sortable>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <div class="block">
        <el-pagination
          next-text="下一页"
          :current-page="currentPage"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="pageSize"
          layout=" sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <dialogs ref="child" />
  </div>
</template>

<script>
import echarts from "echarts";
import dialogs from "@/layout/components/dialog/index";

export default {
  components: {
    dialogs,
  },
  data() {
    return {
      pointValue: ["浏览量", "访客数"],
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
      xData: [],
      yData: [],
      myChartStyle: { float: "left", width: "100%", height: "400px" }, //图表样式
      pv: [],
      uv: [],
      visitCount: [],
      ipCount: [],
      bounceRate: [],
      time: [],
      headLege: [],
      pointValue: ["浏览量", "访客数", "IP数"],
      disabledSelect: [],
      seriesdata: [],
      userListData: [],
      current: {
        size: 10,
        page: 1,
      },
      total: 0,
      pageSize: 10,
      currentPage: 1,
    };
  },
  mounted() {
    this.initEcharts();
    this.initChartValue(); //是否初始开启三个禁用
  },
  beforeDestroy() {
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
    this.chart = null;
  },
  methods: {
    getChannelList(val) {
      this.pv = [];
      this.uv = [];
      this.visitCount = [];
      this.ipCount = [];
      this.bounceRate = [];
      this.xData = [];
      this.sourceChartList = val;
      this.sourceChartList.map((item) => {
        if (item.channel) {
          this.xData.push(item.channel);
        } else {
          this.xData.push(0);
        }
      });
      this.filterData(this.pointValue);
    },
    getUserListEvent(val) {
      this.userListData = val.rows;
      val.rows.map(item=>{
        if (item.avgPv) {
          item.avgPv = Math.floor(item.avgPv)
        }
      })
      this.total = val.total;
      // console.log(this.userListData, "用户列表数据");
    },
    handleCellClick(val) {
      this.$refs.child.callMethod(val);
    },
    
    // 切换事件
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
    initChartValue() {
      let result = [];
      for (let i = 0; i < this.options.length; i++) {
        result.push(this.options[i].label);
        if (this.pointValue.length > 2) {
          this.disabledSelect = result.filter((item) => {
            return this.pointValue.indexOf(item) == -1;
          });
        } else {
          this.disabledSelect = [];
        }
      }
    },
    filterData(val) {
      const { pv, uv, visitCount, ipCount, bounceRate } = this;
      if (val.includes("浏览量")) {
        this.sourceChartList.map((item) => {
          if (item.visitorChannel.pv) {
            return pv.push(item.visitorChannel.pv);
          } else {
            pv.push(0);
          }
        });
      } else {
        this.pv = [];
      }
      if (val.includes("访客数")) {
        this.sourceChartList.map((item) => {
          if (item.visitorChannel.uv) {
            return uv.push(item.visitorChannel.uv);
          } else {
            uv.push(0);
          }
        });
      } else {
        this.uv = [];
      }
      if (val.includes("IP数")) {
        this.sourceChartList.map((item) => {
          if (item.visitorChannel.ipCount) {
            return ipCount.push(item.visitorChannel.ipCount);
          } else {
            ipCount.push(0);
          }
        });
      } else {
        this.ipCount = [];
      }
      if (val.includes("访问次数")) {
        this.sourceChartList.map((item) => {
          if (item.visitorChannel.visitCount) {
            return visitCount.push(item.visitorChannel.visitCount);
          } else {
            visitCount.push(0);
          }
        });
      } else {
        this.visitCount = [];
      }

      if (val.includes("跳出率")) {
        this.sourceChartList.map((item) => {
          if (item.visitorChannel.bounceRate) {
            return bounceRate.push(item.visitorChannel.bounceRate);
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
          barGap: "100%", //柱图间距
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
            color: "#2c7be5",
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
            interval: 0,
            // rotate: "30",
          },
          axisLine: {
            onZero: false,
          },
          axisTick: {
            show: false,
            alignWithLabel: true,
          },
        },
        grid: {
          bottom: "22%",
        },
        legend: {
          data: this.pointValue,
          top: "0%",
        },
        dataZoom: [
          {
            type: "inside",
            startValue: 0,
            endValue: 4,
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
      myChart.setOption(sourceChart, true);
      window.addEventListener("resize", () => {
        myChart.resize();
      });
    },
    handleSizeChange(val) {
      this.current.size = val;
      this.$emit("currentPage", this.current);
    },
    handleCurrentChange(val) {
      this.current.page = val;
      this.$emit("currentPage", this.current);
    },
  },
};
</script>
<style lang="scss" scoped>
@import "~@/styles/components/custom-select.scss";
// ::v-deep .setTable {
//   .el-table--border {
//     border: none;
//   }
//   .el-table th {
//     background-color: transparent !important;
//   }

//   .el-table,
//   .el-table__expanded-cell {
//     background-color: transparent;
//   }

//   .setTable .el-table th,
//   .el-table tr {
//     background-color: transparent;
//   }
// }
.trafficHead {
  font-size: 16px;
  font-weight: 600;
  color: #4d4d4d;
  padding: 0px 30px 20px;
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
  //   position: relative;
  font-size: 14px;
  font-weight: 500;
  line-height: 33px;
  color: #4d4d4d;
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

// table表格
.search_wrappy {
  position: relative;
  margin: 15px;
  background-color: #fff;
  min-height: 647px;
}
.search_table {
  height: 100%;
  padding: 18px 22px;
  // span {
  //   font-size: 13px;
  //   font-weight: 500;
  //   line-height: 31px;
  //   color: #4d4d4d;
  // }
}
.block {
  margin: 20px 12px;
}
::v-deep {
  .el-pagination {
    position: relative;
    width: 100%;
    display: flex;
    justify-content: flex-end;
    padding-right: 10px;
  }
  .el-pagination__jump {
    position: absolute;
    left: 0;
  }
}
</style>
