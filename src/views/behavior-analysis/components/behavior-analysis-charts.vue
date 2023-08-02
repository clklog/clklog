<template>
  <div>
    <div class="chartsIcon">
      <div class="chartLeft">
        <div class="trendHead">
          <div class="trafficHead">指标分析图</div>
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

    <div class="search_wrappy">
      <div class="search_table">
        <span>用户列表</span>
        <div class="setTable">
          <el-table
            :data="tableData"
            :header-cell-style="{ textAlign: 'center' }"
            :cell-style="{ textAlign: 'center' }"
            style="width: 100%; margin-top: 12px; height: 98%"
            :default-sort="{ prop: 'date', order: 'descending' }"
          >
            <el-table-column prop="date" label="日期" sortable width="180">
            </el-table-column>
            <el-table-column prop="word" label="访客ID" sortable width="180">
            </el-table-column>
            <el-table-column prop="visitor" label="访客类型" sortable>
            </el-table-column>
            <el-table-column prop="user" label="访客次数" sortable>
            </el-table-column>
            <el-table-column prop="timeLegth" label="浏览量" sortable>
            </el-table-column>
            <el-table-column prop="pageNum" label="停留时长" sortable>
            </el-table-column>
            <el-table-column prop="out" label="访客页数" sortable>
            </el-table-column>
            <el-table-column prop="out" label="上次访问时间" sortable>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <div class="block">
        <el-pagination
          next-text="下一页"
          :current-page="currentPage4"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="10"
          layout=" sizes, prev, pager, next, jumper"
          :total="40"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
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
      xData: ["网站", "安卓"], //横坐标
      yData: [23, 24], //人数数据
      taskDate: [10, 11],
      averageData: [23, 12],
      myChartStyle: { float: "left", width: "100%", height: "400px" }, //图表样式
      currentPage4: 4,
      tableData: [
        {
          date: "2023/5/30",
          word: "女士衣服",
          visitor: "86",
          user: "15%",
          timeLegth: "695",
          pageNum: "56",
          out: "61",
        },
        {
          date: "2023/5/30",
          word: "女士衣服",
          visitor: "86",
          user: "15%",
          timeLegth: "695",
          pageNum: "56",
          out: "61",
        },
        {
          date: "2023/5/31",
          word: "NA士衣服",
          visitor: "86",
          user: "15%",
          timeLegth: "69",
          pageNum: "856",
          out: "61",
        },
        {
          date: "2023/5/3",
          word: "衣服",
          visitor: "86",
          user: "15%",
          timeLegth: "695",
          pageNum: "86",
          out: "61",
        },
        {
          date: "2023/5/13",
          word: "士衣服",
          visitor: "186",
          user: "151%",
          timeLegth: "695",
          pageNum: "856",
          out: "61",
        },
        {
          date: "2023/5/13",
          word: "士衣服",
          visitor: "186",
          user: "151%",
          timeLegth: "695",
          pageNum: "856",
          out: "61",
        },
        {
          date: "2023/5/13",
          word: "士衣服",
          visitor: "186",
          user: "151%",
          timeLegth: "695",
          pageNum: "856",
          out: "61",
        },
        {
          date: "2023/5/13",
          word: "士衣服",
          visitor: "186",
          user: "151%",
          timeLegth: "695",
          pageNum: "856",
          out: "61",
        },
        {
          date: "2023/5/13",
          word: "士衣服",
          visitor: "186",
          user: "151%",
          timeLegth: "695",
          pageNum: "856",
          out: "61",
        },
        {
          date: "2023/5/13",
          word: "士衣服",
          visitor: "186",
          user: "151%",
          timeLegth: "695",
          pageNum: "856",
          out: "61",
        },
        {
          date: "2023/5/13",
          word: "士衣服",
          visitor: "186",
          user: "151%",
          timeLegth: "695",
          pageNum: "856",
          out: "61",
        },
      ],
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
          data: ["浏览量", "访问次数", "平均访问页数"],
          top: "0%",
        },
        yAxis: {},
        series: [
          {
            type: "bar", //形状为柱状图
            data: this.yData,
            barWidth: 40,
            name: "浏览量", // legend属性
            label: {
              // 柱状图上方文本标签，默认展示数值信息
              show: true,
              position: "top",
            },
            itemStyle: {
              color: "#051e71",
            },
          },
          {
            type: "bar", //形状为柱状图
            data: this.taskDate,
            barWidth: 40,
            name: "访问次数", // legend属性
            label: {
              // 柱状图上方文本标签，默认展示数值信息
              show: true,
              position: "top",
            },
            itemStyle: {
              color: "#3d64e6",
            },
          },
          {
            type: "bar", //形状为柱状图
            data: this.averageData,
            barWidth: 40,
            name: "平均访问页数", // legend属性
            label: {
              show: true,
              position: "top",
            },
            itemStyle: {
              color: "#A4C4FE",
            },
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
    handleCurrentChange() {},
    handleSizeChange() {},
  },
};
</script>
<style lang="scss"></style>

<style lang="scss" scoped>
::v-deep .setTable {
  .el-table--border {
    border: none;
  }
  .el-table th {
    background-color: transparent !important;
  }

  .el-table,
  .el-table__expanded-cell {
    background-color: transparent;
  }

  .setTable .el-table th,
  .el-table tr {
    background-color: transparent;
  }
}
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
  span {
    font-size: 13px;
    font-weight: 500;
    line-height: 31px;
    color: #4d4d4d;
  }
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
