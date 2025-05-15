<template>
  <div v-loading="loadingRefresh" class="userLoyalty public-hoverItem">
    <!-- 测试台湾地图展示内容-->
    <!-- <div ref="map" id="chartOne" style="width: 800px; height: 600px"></div> -->
    <el-tabs @tab-click="handleClick" v-model="activeTab">
      <el-tab-pane label="数据汇总" name="数据汇总">
        <div class="tab-warry block-main" style="padding-top: 0">
          <div class="tab-sing">
            <div style="display: flex">
              <el-input
                class="custom_input"
                placeholder="请输入应用版本"
                style="width: 200px; border-radius: 4px"
                v-model="version"
                @keyup.enter.native="searchEvent()"
                clearable
                @clear="handleClear"
              >
                <i
                  @click="searchEvent"
                  slot="prefix"
                  class="el-input__icon el-icon-search"
                ></i>
              </el-input>
            </div>
            <div class="warry_select">
              <div class="title">操作系统:</div>
              <el-select
                style="width: 100px"
                v-model="channelValue"
                class="single_select"
                @change="changSystemEvent('数据汇总')"
              >
                <el-option
                  v-for="item in allChaneList"
                  :key="item.id"
                  :label="item.label"
                  :value="item.id"
                >
                </el-option>
              </el-select>
            </div>
            <!-- <div class="warry_select">
              <div class="title">崩溃原因:</div>
              <el-select
                style="width: 100px"
                v-model="selectVal"
                class="single_select"
                placeholder=""
              >
                <el-option
                  v-for="item in optionList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </div> -->
          </div>
          <div class="table-block">
            <el-table
              :header-cell-style="{
                textAlign: 'center',
                background: '#f7fafe ',
              }"
              :cell-style="{ textAlign: 'center' }"
              class="public-radius"
              border
              :data="dataSumList"
              style="width: 100%"
            >
              <el-table-column
                label="序号"
                type="index"
                width="80"
                align="center"
              >
                <template slot-scope="scope">
                  <span v-text="getIndex(scope.$index)"> </span>
                </template>
              </el-table-column>
              <el-table-column prop="appVersion" label="应用版本" width="180">
              </el-table-column>

              <el-table-column prop="channel" label="操作系统" width="180">
              </el-table-column>
              <el-table-column prop="" label="设备型数" width="180">
                <template slot-scope="{ row }">
                  <div
                    style="
                      display: flex;
                      justify-content: center;
                      position: relative;
                    "
                    @click="handleRowClick(row)"
                  >
                    <div style="text-align: center">{{ row.modelCount }}</div>

                    <img
                      src="@/assets/images/showFlag.png"
                      style="
                        width: 13px;
                        height: 13px;
                        position: absolute;
                        right: 0;
                        cursor: pointer;
                      "
                    />
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="visitCount" label="访问次数">
              </el-table-column>
              <el-table-column prop="crashedCount" label="崩溃次数">
              </el-table-column>
              <el-table-column prop="pvRate" label="崩溃率">
                <template slot-scope="{ row }">
                  {{ row.pvRate | percentage }}
                </template>
              </el-table-column>
              <el-table-column prop="uv" label="访问用户数"> </el-table-column>
              <el-table-column prop="crashedUv" label="崩溃触发用户数">
              </el-table-column>

              <el-table-column prop="uvRate" label="崩溃触发用户占比">
                <template slot-scope="{ row }">
                  {{ row.uvRate | percentage }}
                </template>
              </el-table-column>
              <el-table-column prop="pvRate" label="崩溃数占比">
                <template slot-scope="{ row }">
                  {{ row.pvRate | percentage }}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="崩溃日志" name="崩溃日志">
        <div class="tab-warry block-main" style="padding-top: 0">
          <div class="tab-sing">
            <el-input
              class="custom_input"
              placeholder="请输入应用版本"
              v-model="version"
              style="width: 200px; border-radius: 4px"
              @keyup.enter.native="searchEvent()"
              @clear="handleClear"
              clearable
            >
              <i
                @click="searchEvent"
                slot="prefix"
                class="el-input__icon el-icon-search"
              ></i>
            </el-input>
            <div class="warry_select">
              <div class="title">操作系统:</div>
              <el-select
                style="width: 100px"
                v-model="channelValue"
                class="single_select"
                placeholder=""
                @change="changSystemEvent('崩溃日志')"
              >
                <el-option
                  v-for="item in allChaneList"
                  :key="item.id"
                  :label="item.label"
                  :value="item.id"
                >
                </el-option>
              </el-select>
            </div>
          </div>
          <div class="table-block">
            <el-table
              :header-cell-style="{
                textAlign: 'center',
                background: '#f7fafe ',
              }"
              :cell-style="tableHeaderColor"
              class="public-radius"
              border
              :data="crashLogList"
              style="width: 100%"
            >
              <el-table-column
                label="序号"
                type="index"
                width="80"
                align="center"
              >
                <template slot-scope="scope">
                  <span v-text="getIndex(scope.$index)"> </span>
                </template>
              </el-table-column>
              <el-table-column prop="logTime" label="崩溃时间" width="200">
              </el-table-column>
              <el-table-column prop="appVersion" label="应用版本">
              </el-table-column>
              <el-table-column prop="os" label="操作系统"> </el-table-column>
              <el-table-column prop="osVersion" label="操作系统版本">
              </el-table-column>

              <el-table-column
                prop="model"
                label="设备型号"
                :show-overflow-tooltip="true"
                min-width="200"
              >
              </el-table-column>
              <el-table-column
                prop="appCrashedReason"
                width="250"
                label="崩溃详情信息"
              >
                <!-- :show-overflow-tooltip="true" -->
                <template slot-scope="{ row }">
                  <div
                    style="
                      display: flex;
                      justify-content: center;
                      align-items: center;
                      position: relative;
                    "
                    @click="handleDetailDialog(row)"
                  >
                    <div
                      style="
                        text-align: center;
                        width: 200px;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                        margin-right: 15px;
                        cursor: pointer;
                      "
                    >
                      {{ row.appCrashedReason }}
                    </div>
                    <i
                      v-if="row.appCrashedReason"
                      class="el-icon-view"
                      style="color: #2c7be5; cursor: pointer"
                    ></i>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="eventSessionId" width="325" label="会话ID">
                <template slot-scope="{ row }">
                  <div
                    style="
                      display: flex;
                      justify-content: center;
                      position: relative;
                    "
                  >
                    <div
                      style="
                        text-align: left;
                        width: 325px;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                        margin-right: 15px;
                        cursor: pointer;
                      "
                    >
                      {{ row.eventSessionId }}
                    </div>

                    <!-- <i
                      @click="copyRowId(row.eventSessionId)"
                      class="el-icon-document-copy"
                      style="
                        width: 13px;
                        height: 13px;
                        position: absolute;
                        right: 0;
                        cursor: pointer;
                        color: #2c7be5;
                      "
                    ></i> -->
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div class="block">
            <el-pagination
              next-text="下一页"
              :current-page="pageNum"
              :page-sizes="[10, 20, 30, 40]"
              :page-size="pageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    <dialogCollapase ref="dialogCollapase"></dialogCollapase>
    <collapseDetail ref="collapseDetail"></collapseDetail>
  </div>
</template>

<script>
import echarts from "echarts";
import taiwan from "echarts/map/json/province/taiwan.json";
import { copyObj } from "@/utils/copy";
import { getPageApi, groupedSummaryApi } from "@/api/trackingapi/collapse";
import dialogCollapase from "./dialog";
import collapseDetail from "./collapseDetail-dialog.vue";
export default {
  components: {
    dialogCollapase,
    collapseDetail,
  },
  data() {
    return {
      loadingRefresh: false,
      pickerBeginOption: {
        disabledDate(date) {
          const today = new Date();
          return date.getTime() > today.getTime();
        },
        shortcuts: [
          {
            text: "近一周",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "近一个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "近三个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "近六个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 180);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "近一年",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 365);
              picker.$emit("pick", [start, end]);
            },
          },
        ],
      },
      commonParams: {},
      activeTab: "数据汇总",
      version: "",
      channelValue: "",
      optionList: [],
      pageNum: 1,
      pageSize: 10,
      selectVal: "",
      total: 0,
      currentTime: [],
      dataSumList: [],
      crashLogList: [],
      allChaneList: [
        { label: "全部", id: "" },
        { label: "安卓", id: "安卓" },
        { label: "苹果", id: "苹果" },
      ],
    };
  },
  mounted() {},
  methods: {
    copyRowId(id) {
      const textarea = document.createElement("textarea");
      textarea.value = id;
      document.body.appendChild(textarea);
      textarea.select();
      document.execCommand("copy");
      document.body.removeChild(textarea);
      this.$message.success("复制成功！");
    },
    initMap() {
      echarts.registerMap("taiwan", taiwan);
      const myChart = echarts.init(document.getElementById("chartOne"));
      var option = {
        visualMap: {
          min: 0,
          max: 1000,
          realtime: false,
          calculable: true,
          inRange: {
            // color: ["lightskyblue", "yellow", "orangered"],
            color: ["#fff", "#2c7be5"],
          },
        },
        tooltip: {
          // 悬浮框
          trigger: "item", // 触发条件
          backgroundColor: "RGBA(136, 123, 135, 0.8)",
          formatter: "{b}<br/>案件数{c}", // 自定义显示数据
          textStyle: {
            color: "#ffffff",
            fontSize: 8,
          },
        },
        series: [
          {
            type: "map",
            map: "taiwan",
            zoom: 1.1,
            // top: "10%",
            // x: "center",
            label: {
              show: true, // 显示
              textStyle: {
                fontSize: 8,
                position: "insideTop",
              },
            },
            itemStyle: {
              normal: {
                borderColor: "rgba(0, 0, 0, 0.2)",
              },
              emphasis: {
                shadowOffsetX: 0,
                shadowOffsetY: 0,
                shadowBlur: 20,
                borderWidth: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
              },
            },
            // 数据
            data: [
              { name: "台北市", value: 100 },
              { name: "高雄市", value: 200 },
              // ... 其他市县数据
            ],
          },
        ],
      };
      myChart.setOption(option);
    },
    tableHeaderColor({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 6) {
        return "text-align:left";
      } else {
        return "text-align:center";
      }
    },
    handleDetailDialog(row) {
      this.$refs.collapseDetail.openErrorLog(row);
    },
    handleRowClick(row) {
      this.$refs.dialogCollapase.openDialogEvent(row, this.commonParams);
    },
    getIndex($index) {
      return (this.pageNum - 1) * this.pageSize + $index + 1;
    },
    paramsEvent(params) {
      this.commonParams = copyObj(params);
      this.currentTime = [params.startTime, params.endTime];
      this.checkApiType(this.activeTab);
    },
    changSystemEvent(type) {
      // this.checkApiType(type);
      this.checkApiType(this.activeTab);
    },
    searchEvent() {
      this.checkApiType(this.activeTab);
    },
    handleClear() {
      this.checkApiType(this.activeTab);
    },
    checkDatePick(time) {
      this.checkApiType(this.activeTab);
    },
    checkApiType(type) {
      this.commonParams.pageSize = this.pageSize;
      this.commonParams.pageNum = this.pageNum;
      this.commonParams.version = this.version;
      this.commonParams.channel = this.channelValue ? [this.channelValue] : [];
      this.loadingRefresh = true;
      if (type == "数据汇总") {
        let params = copyObj(this.commonParams);
        delete params.pageNum;
        delete params.pageSize;
        groupedSummaryApi(params)
          .then((res) => {
            if (res.code == 200) {
              this.loadingRefresh = false;
              // this.total = res.data.total;
              this.dataSumList = res.data;
            }
          })
          .catch(() => {
            this.loadingRefresh = false;
          });
      } else {
        let params = copyObj(this.commonParams);
        params.startTime = this.currentTime[0];
        params.endTime = this.currentTime[1];
        getPageApi(params)
          .then((res) => {
            if (res.code == 200) {
              this.loadingRefresh = false;
              this.total = res.data.total;
              this.crashLogList = res.data.rows;
            }
          })
          .catch(() => {
            this.loadingRefresh = false;
          });
      }
    },
    handleClick(val) {
      this.pageSize = 10;
      this.pageNum = 1;
      this.checkApiType(this.activeTab);
    },
    handleSizeChange(val) {
      this.pageNum = 1;
      this.pageSize = val;
      this.checkApiType("崩溃日志");
    },
    handleCurrentChange(val) {
      this.pageNum = val;
      this.checkApiType("崩溃日志");
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep {
  @import "~@/styles/components/el-pagination.scss";
  // 设置input
  .el-input__inner {
    font-size: 11px;
  }
  .custom_input {
    margin-right: 20px;
  }
  .el-button--medium {
    padding: 0 20px;
  }
  .el-input--medium .el-input__inner {
    height: 32px;
    line-height: 32px;
  }

  .el-input--medium .el-input__icon {
    line-height: 32px;
  }
  // end

  .el-tabs__header {
    background-color: #f9fafd;
    margin: 0 0;
    border-radius: 6px 6px 0 0;
  }
  .el-tabs__item.is-active {
    background-color: #fff;
    border-radius: 6px 0 0 0;
  }
  .el-tabs__nav-wrap::after {
    background-color: #fff;
  }
  .el-tabs__item {
    padding: 0 0;
    width: 130px;
    height: 65px;
    line-height: 65px;
    text-align: center;
    font-size: 15px;
    font-weight: bold;
    color: #4d4d4d;
  }

  .single_select .el-input__inner {
    border-radius: 0px;
    border-top-width: 0px;
    border-left-width: 0px;
    border-right-width: 0px;
    border-bottom-width: 1px;
    border-bottom: 1px solid #acb2ba;
    background-color: transparent;
    font-size: 12px;
    height: 30px;
    line-height: 30px;
    border-bottom-width: 0;
    padding-left: 10px;
    width: 100px;
  }

  .el-input--medium .el-input__icon {
    line-height: 30px;
    cursor: pointer;
  }
  // 日历样式start
  .el-range-editor--medium .el-range-input {
    font-size: 13px;
  }
  .el-date-editor .el-range__icon {
    font-size: 13px;
    line-height: 24px;
  }
  .el-range-editor--medium .el-range-separator {
    font-size: 13px;
    line-height: 24px;
  }
}
.userLoyalty {
  margin: 20px;
  background: #fff;
  border-radius: 6px !important;
  .tab-warry {
    min-height: 500px;
    .tab-sing {
      display: flex;
      margin: 20px 0;
      justify-content: flex-end;
    }
    .table-block {
      min-height: 400px;
    }
    .warry_select {
      display: flex;
      border: 1px solid #d5dfed;
      border-radius: 4px;
      align-items: center;
      min-height: 30px;
      margin-right: 20px;
      width: 153px;
      .title {
        font-size: 12px;
        color: #4d4d4d;
        padding-left: 6px;
        white-space: nowrap;
      }
    }
  }
}
</style>
