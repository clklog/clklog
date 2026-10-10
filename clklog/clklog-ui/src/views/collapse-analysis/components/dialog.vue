<template>
  <div>
    <el-dialog
      ref="customDialog"
      :visible.sync="eventDialogVisible"
      :modal-append-to-body="false"
      :show-close="false"
      :close-on-press-escape="false"
      width="80vw"
      top="2vh"
      style="overflow-y: auto; box-sizing: border-box"
    >
      <div>
        <div class="custom-header">
          <div>
            <div
              style="
                height: 44px;
                background: #fbfcfe;
                display: flex;
                justify-content: space-between;
                align-items: center;
                border-radius: 6px 6px 0 0;
              "
            >
              <div
                style="
                  font-size: 16px;
                  font-weight: bold;
                  line-height: 44px;
                  margin-left: 16px;
                  color: #4d4d4d;
                "
              >
                崩溃数据
              </div>
              <div @click="handleClose" class="closeIcon">
                <i class="el-icon-close"></i>
              </div>
            </div>
          </div>
          <div
            class="public-hoverItem"
            style="margin: 20px; box-sizing: border-box"
          >
            <eventBar
              @setFilterBarParams="setFilterBarParams"
              class="event_x"
              ref="eventBar"
            ></eventBar>
          </div>
        </div>

        <div
          style="
            min-height: 400px;
            max-height: 1000px;
            width: 80vw;
            overflow: auto;
            position: relative;
          "
        >
          <div v-loading="loadingRefresh" class="userLoyalty public-hoverItem">
            <el-tabs
              @tab-click="handleClick"
              v-model="activeTab"
              class="custom_tab"
            >
              <el-tab-pane label="崩溃设备数据汇总" name="崩溃设备数据汇总">
                <div class="tab-warry block-main">
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
                          <span v-text="getSumIndex(scope.$index)"> </span>
                        </template>
                      </el-table-column>
                      <el-table-column
                        prop="appVersion"
                        label="应用版本"
                        width="180"
                      >
                      </el-table-column>

                      <el-table-column
                        prop="channel"
                        label="操作系统"
                        width="180"
                      >
                      </el-table-column>
                      <el-table-column
                        prop="model"
                        label="设备型号"
                        width="180"
                      >
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
                      <el-table-column prop="uv" label="访问用户数">
                      </el-table-column>
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
                  <div class="block">
                    <el-pagination
                      next-text="下一页"
                      :current-page="sum_pageNum"
                      :page-sizes="[10, 20, 30, 40]"
                      :page-size="sum_pageSize"
                      layout="total, sizes, prev, pager, next, jumper"
                      :total="sum_total"
                      @size-change="handleSizeChangeSum"
                      @current-change="handleCurrentChangeSum"
                    />
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="崩溃日志" name="崩溃日志">
                <div class="tab-warry block-main">
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
                      <el-table-column
                        prop="logTime"
                        label="崩溃时间"
                        width="170"
                      >
                      </el-table-column>
                      <el-table-column prop="appVersion" label="应用版本">
                      </el-table-column>
                      <el-table-column prop="os" label="操作系统" width="180">
                      </el-table-column>
                      <el-table-column prop="osVersion" label="操作系统版本">
                      </el-table-column>
                      <el-table-column prop="model" label="设备型号">
                      </el-table-column>
                      <el-table-column
                        prop="appCrashedReason"
                        width="200"
                        :show-overflow-tooltip="false"
                        label="崩溃详情信息"
                      >
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
                      <el-table-column
                        prop="eventSessionId"
                        min-width="320"
                        label="会话ID"
                      >
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
          </div>
        </div>
      </div>
    </el-dialog>
    <collapseDetail ref="collapseDetail"></collapseDetail>
  </div>
</template>

<script>
import echarts from "echarts";
import { copyObj } from "@/utils/copy";
import eventBar from "./eventBar";
import { getPageApi, getPagedSummaryApi } from "@/api/trackingapi/collapse";
import collapseDetail from "./collapseDetail-dialog.vue";
export default {
  data() {
    return {
      dataList: [],
      activeTab: "崩溃设备数据汇总",
      eventDialogVisible: false,
      loadingRefresh: false,
      filterBarParams: {},
      rowList: {},

      tableList: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,

      dataSumList: [],
      sum_total: 0,
      sum_pageSize: 10,
      sum_pageNum: 1,
      crashLogList: [],
      commonParams: {},
    };
  },
  components: {
    eventBar,
    collapseDetail,
  },
  mounted() {},
  computed: {
    projectName() {
      return this.$store.getters.projectName;
    },
    proto1() {
      let a = `eventProp.`;
      return a;
    },
  },
  methods: {
    handleDetailDialog(row) {
      this.$refs.collapseDetail.openErrorLog(row);
    },
    tableHeaderColor({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 6) {
        return "text-align:left";
      } else {
        return "text-align:center";
      }
    },
    // 崩溃日志
    getLogList() {
      this.commonParams.pageSize = this.pageSize;
      this.commonParams.pageNum = this.pageNum;
      this.loadingRefresh = true;
      getPageApi(this.commonParams)
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
    },
    // 汇总
    getPagedSummary() {
      this.loadingRefresh = true;
      this.commonParams.pageSize = this.sum_pageSize;
      this.commonParams.pageNum = this.sum_pageNum;
      getPagedSummaryApi(this.commonParams)
        .then((res) => {
          if (res.code == 200) {
            this.loadingRefresh = false;
            this.sum_total = res.data.total;
            this.dataSumList = res.data.rows;
          }
        })
        .catch(() => {
          this.loadingRefresh = false;
        });
    },
    openDialogEvent(row, params) {
      this.initPaginaton();
      this.activeTab = "崩溃设备数据汇总";
      this.commonParams = copyObj(params);
      this.rowList = copyObj(row);
      if (this.rowList.channel == "Android") {
        this.rowList.channel = "安卓";
      } else {
        this.rowList.channel = "苹果";
      }
      this.commonParams.model = "";
      this.commonParams.channel = this.rowList.channel
        ? [this.rowList.channel]
        : [];
      this.commonParams.version = row.appVersion;
      this.commonParams.eventName = this.rowList.eventName;

      this.eventDialogVisible = true;
      this.$nextTick(() => {
        this.$refs.eventBar.statisticEvent(this.commonParams, 1);
      });
      this.checkTabLable(this.activeTab);
    },
    checkTabLable(tab) {
      if (tab == "崩溃设备数据汇总") {
        this.getPagedSummary();
      } else {
        this.getLogList();
      }
    },

    handleClick() {
      this.pageNum = 1;
      this.sum_pageNum = 1;
      this.checkTabLable(this.activeTab);
    },
    handleClose() {
      this.eventDialogVisible = false;
    },
    // 切换参数
    setFilterBarParams(val) {
      this.pageNum = 1;
      this.sum_pageNum = 1;
      let params = copyObj(val);
      this.commonParams.endTime = params.endTime;
      this.commonParams.startTime = params.startTime;
      this.commonParams.channel = params.channel ? [params.channel] : [];
      this.commonParams.version = params.version;
      this.commonParams.model = params.model;
      this.checkTabLable(this.activeTab);
    },
    initPaginaton() {
      this.pageNum = 1;
      this.sum_pageNum = 1;
      this.pageSize = 10;
      this.sum_pageSize = 10;
    },
    getSumIndex($index) {
      return (this.sum_pageNum - 1) * this.sum_pageSize + $index + 1;
    },

    getIndex($index) {
      return (this.pageNum - 1) * this.pageSize + $index + 1;
    },

    handleSizeChange(val) {
      this.pageNum = 1;
      this.pageSize = val;
      this.getLogList();
    },
    handleCurrentChange(val) {
      this.pageNum = val;
      this.getLogList();
    },
    handleSizeChangeSum(val) {
      this.sum_pageNum = 1;
      this.sum_pageSize = val;
      this.getPagedSummary();
    },
    handleCurrentChangeSum(val) {
      this.sum_pageNum = val;
      this.getPagedSummary();
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep {
  @import "~@/styles/components/el-checkbox.scss";
  @import "~@/styles/components/el-pagination.scss";
  .custom_tab {
    .el-tabs__item {
      // padding: 0 10px;
      width: 150px;
    }
  }
  .event_x .headerFilter .tabBar {
    // position: relative !important;
  }
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
}
.tab-warry {
  min-height: 500px;
  border-radius: 6px;
}
.userLoyalty {
  margin: 20px;
  margin-top: 0;
  box-sizing: border-box;
  background: #fff;
  position: relative;
  border-radius: 6px !important;
}
.con_tip {
  width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 20px;
}
::v-deep {
  .el-dialog {
    border-radius: 6px;
  }
  .el-dialog__headerbtn {
    top: 15px;
  }
  .el-dialog__header {
    padding: 0 !important;
    border-radius: 6px;
  }

  .el-dialog__body {
    padding: 0;
    background-color: #eef4fd;
    border-radius: 6px;
  }
}
.closeIcon {
  margin-right: 30px;
  cursor: pointer;
  font-size: 20px;
}
.closeIcon:hover {
  color: #2c7be5;
}
.trendDiv {
  min-height: 462px;
  background-color: #fff;
  border-radius: 6px;
}

.setSpace {
  margin-bottom: 8px;
  margin-left: 25px;
}
.setBody {
  margin-bottom: 8px;
}
.mapTable {
  width: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
