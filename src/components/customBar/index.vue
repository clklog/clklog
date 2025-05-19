<template>
  <div>
    <div class="documentation-container">
      <div class="headerFilter">
        <!-- 第一行 -->
        <div class="tabBar">
          <div style="display: flex; align-items: center; white-space: nowrap">
            <div class="check_item public_border_color" style="height: 30">
              <span>时间:</span>
              <!-- 单选事件 -->
              <el-radio-group
                v-model="timeFlag"
                class="checkBoxStyle"
                @change="handleChange"
              >
                <el-radio label="day">今日</el-radio>
                <el-radio label="previous">昨日</el-radio>
                <el-radio label="week">过去7天</el-radio>
                <el-radio label="month">过去30天</el-radio>
              </el-radio-group>
            </div>
            <!-- 日期1 -->
            <el-date-picker
              class="timnePickCSS"
              style="margin-left: 20px; width: 250px; height: 30px"
              v-model="currentTime"
              ref="tiemPick"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd"
              :picker-options="pickerBeginOption"
              @change="checkDateEvnet"
            >
            </el-date-picker>
            <div style="margin-left: 20px; line-height: 30px" v-if="ByContrast">
              <el-checkbox v-model="checked" @change="checkTimeSlot">{{
                checkLabel
              }}</el-checkbox>
            </div>

            <div v-if="ByDayOrHour">
              <el-radio-group
                size="mini"
                v-model="timeType"
                style="font-size: 13px; height: 30px; margin-left: 20px"
              >
                <!-- <el-radio-button label="hour" :disabled="dayFlag ? true : false"
                  >按时</el-radio-button
                > -->
                <el-radio-button label="day" :disabled="dayFlag ? true : false"
                  >按日</el-radio-button
                >
                <el-radio-button
                  label="week"
                  :disabled="weekFlag ? true : false"
                  >按周</el-radio-button
                >
                <el-radio-button
                  label="month"
                  :disabled="monthFlag ? true : false"
                  >按月</el-radio-button
                >
              </el-radio-group>
            </div>

            <div
              :style="
                device == 'desktop'
                  ? 'position: fixed; right: 0px'
                  : 'position:relative;margin-left:50px;margin-right:30px'
              "
              v-if="ByRefresh"
            >
              <!-- <el-button
                class="custom_button"
                icon="el-icon-refresh-right"
                :disabled="isDisableBtn"
                @click="customAnalysisRefreshApi"
                >刷新</el-button
              > -->
              <div class="tabBarBtn" style="position: relative">
                <el-button
                  class="zc_btn refresh"
                  icon="el-icon-refresh-right"
                  :disabled="isDisableBtn"
                  @click="customAnalysisRefreshApi"
                  >刷新</el-button
                >
                <el-button
                  icon="el-icon-refresh"
                  @click="collapseResetApiList"
                  class="zc_btn_default reset"
                  >重置</el-button
                >
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import _ from "lodash";
import { copyObj } from "@/utils/copy";
export default {
  props: {
    ByEventGrop: {
      type: Boolean,
      default: false,
    },
    ByReceive: {
      type: Boolean,
      default: false,
    },
    ByGrop: {
      type: Boolean,
      default: false,
    },
    ByTag: {
      type: Boolean,
      default: false,
    },
    // 对比事件
    ByContrast: {
      type: Boolean,
      default: false,
    },
    BySearchWord: {
      type: Boolean,
      default: false,
    },
    ByRefresh: {
      type: Boolean,
      default: false,
    },
    ByCollapse: {
      type: Boolean,
      default: false,
    },
    ByDayOrHour: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      isDisableBtn: false, //是否开启刷新
      isNotAnalysis: true, //是否展示开始分析
      monthFlag: true,
      weekFlag: true,
      dayFlag: false,
      timeType: "day",
      groupList: [],
      all: {
        label: "全部",
        value: "",
      },
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
      checked: false,
      contrastFlag: false,
      checkLabel: "对比时间段",
      contrastValue: "", //对比时间
      timeFlag: "day",
      statuList: [
        {
          value: "",
          label: "全部",
        },
      ],
      currentTime: "",
      groupName: "",
      searchWord: "",
      startTime: "",
      endTime: "",
      name: "", //搜索词
      intervalTime: "",
      initNum: 4,
      commonData: "",
      loadingApi: false,
    };
  },
  created() {},
  mounted() {
    // 参数更新
    if (this.$route.query.query) {
      let routeObj = JSON.parse(this.$route.query.query);
      this.startTime = routeObj.startDate;
      this.endTime = routeObj.endDate;
      this.timeType = routeObj.dataType;
      this.currentTime = [this.startTime, this.endTime];
      if (routeObj.timeFlag) {
        this.timeFlag = routeObj.timeFlag;
        this.handleChange(this.timeFlag);
      }
      this.anTimeEcho();
    } else {
      this.initDateHandle();
    }
    this.$bus.$on("$isOpen", (val) => {
      this.isNotAnalysis = val;
    });
    // 初始调用漏斗接口
    this.$bus.$on("$refreshRoute", () => {
      this.customAnalysisRefreshApi();
    });
    this.$bus.$on("$isDisable", (val) => {
      this.isDisableBtn = val;
    });
  },
  computed: {
    projectName() {
      return this.$store.getters.projectName;
    },
    ...mapState({
      device: (state) => (state.app.device ? state.app.device : "desktop"),
    }),
    defaultParams() {
      const { startTime, endTime, timeFlag } = this;
      return {
        startTime,
        endTime,
        timeFlag,
      };
    },
    commonParams() {
      const { name, groupName, timeType } = this;
      let obj = {};
      obj = Object.assign(obj, this.defaultParams);
      if (this.BySearchWord) {
        obj = Object.assign(obj, { name });
      }
      if (this.ByGrop) {
        obj = Object.assign(obj, { groupName });
      }
      if (this.ByDayOrHour) {
        obj = Object.assign(obj, { groupName, timeType });
      }
      return obj;
    },
  },
  watch: {
    commonParams: {
      deep: true,
      immediate: false,
      handler: _.debounce(function (newVal, oldVal) {
        if (JSON.stringify(newVal) !== JSON.stringify(oldVal)) {
          this.commonData = newVal;
          this.$nextTick(() => {
            this.setTopFilterParams(newVal);
          });
        }
      }, 500),
    },
  },
  methods: {
    collapseResetApiList() {
      this.timeType = "day";
      this.timeFlag = "day";
      this.handleChange(this.timeFlag);
    },
    collapseRefreshApi() {
      this.loadingApi = true;
      if (!this.commonData) {
        this.commonData = this.commonParams;
      }
      this.setTopFilterParams(this.commonData);
      setTimeout(() => {
        this.loadingApi = false;
      }, 500);
    },

    customAnalysisRefreshApi() {
      this.$emit("startAnalysiApi");
    },
    // 单选切换时间
    handleChange(val) {
      this.checked = false; //暂定清空日期
      this.contrastFlag = false;
      this.checkLabel = "对比时间段";
      let dateTime = this.toDateNow();
      let toData =
        new Date(new Date().toLocaleDateString()).getTime() + 8 * 3600 * 1000;

      if (val == "day") {
        this.currentTime = [dateTime, dateTime];
        this.dateTimeCount(1);
        this.timeType = "day";
      } else if (val == "previous") {
        this.timeDifference = toData - 3600 * 24 * 1000;
        this.timestampToTime(this.timeDifference);
        this.currentTime = [this.checkDateTime, this.checkDateTime];
        this.dateTimeCount(1);
        this.timeType = "day";
      } else if (val == "week") {
        this.timeDifference = toData - 6 * 3600 * 24 * 1000;
        this.timestampToTime(this.timeDifference);
        this.currentTime = [this.checkDateTime, dateTime];
        this.dateTimeCount(6);
        this.timeType = "day";
      } else {
        this.timeDifference = toData - 29 * 3600 * 24 * 1000;
        this.timestampToTime(this.timeDifference);
        this.currentTime = [this.checkDateTime, dateTime];
        this.dateTimeCount(29);
        this.timeType = "day";
      }
      this.startTime = this.currentTime[0];
      this.endTime = this.currentTime[1];
    },
    // 日期计算
    dateTimeCount(result) {
      if (6 <= result <= 29) {
        this.weekFlag = false;
        this.dayFlag = false;
        this.monthFlag = true;
        switch (this.timeType) {
          case "month":
            this.timeType = "day";
            break;

          default:
            break;
        }
      }
      if (result >= 29) {
        this.monthFlag = false;
        this.weekFlag = false;
        this.dayFlag = false;
      }
      if (result < 6) {
        this.dayFlag = false;
        this.monthFlag = true;
        this.weekFlag = true;
        switch (this.timeType) {
          case "month":
          case "week":
            this.timeType = "day";
            break;
        }
      }
    },

    anTimeEcho() {
      // 按时 按日
      const toDate = this.$options.filters.todateFunc(); //当日
      let timeStamp =
        new Date(this.endTime).getTime() - new Date(this.startTime).getTime();
      if (timeStamp == 0) {
        if (toDate == this.startTime) {
          this.timeFlag = "day";
        } else {
          this.timeFlag = "previous";
        }
      } else if (timeStamp == 518400000) {
        this.timeFlag = "week";
      } else if (timeStamp == 2505600000) {
        this.timeFlag = "month";
      } else {
        this.timeFlag = "";
      }
    },

    searchEvent() {
      this.name = this.searchWord;
    },

    toDateNow() {
      var date = new Date();
      return (
        date.getFullYear() +
        "-" +
        (date.getMonth() + 1 < 10
          ? "0" + (date.getMonth() + 1)
          : date.getMonth() + 1) +
        "-" +
        (date.getDate() < 10 ? "0" + date.getDate() : date.getDate())
      );
    },
    // 时间戳转换器
    timestampToTime(timestamp) {
      var date = new Date(timestamp);
      var Y = date.getFullYear() + "-";
      var M =
        (date.getMonth() + 1 < 10
          ? "0" + (date.getMonth() + 1)
          : date.getMonth() + 1) + "-";
      var D =
        (date.getDate() < 10 ? "0" + date.getDate() : date.getDate()) + "";
      this.checkDateTime = Y + M + D;
    },
    // 默认初始日期
    initDateHandle() {
      var date = new Date();
      let dateTime =
        date.getFullYear() +
        "-" +
        (date.getMonth() + 1 < 10
          ? "0" + (date.getMonth() + 1)
          : date.getMonth() + 1) +
        "-" +
        (date.getDate() < 10 ? "0" + date.getDate() : date.getDate());
      this.currentTime = [dateTime, dateTime];
      this.initDate(this.currentTime); //默认日期
    },
    // 日期初始值
    initDate(val) {
      let startTime = val[0].split("-").join("");
      let endTime = val[1].split("-").join("");
      this.startTime = val[0];
      this.endTime = val[1];
      let result = endTime - startTime;
    },
    setTopFilterParams(val) {
      this.$emit("setFilterBarParams", val);
    },
    checkDateEvnet(val) {
      this.checked = false; //暂定清空日期
      this.contrastFlag = false;
      this.checkLabel = "对比时间段";
      this.contrastValue = "";
      this.timeFlag = "";
      this.startTime = val[0];
      this.endTime = val[1];
      this.changeTimestamp(this.startTime, this.endTime);
    },
    // 计算时间戳
    changeTimestamp(start, end) {
      let endTimed = new Date(end).getTime();
      let startTimed = new Date(start).getTime();
      let timeStamp = endTimed - startTimed;
      if (timeStamp == 0) {
        // this.timeType = "hour";
        this.timeType = "day";
        this.monthFlag = true;
        this.weekFlag = true;
        this.dayFlag = false;
        // 604800000
      } else if (timeStamp <= 604800000) {
        this.timeType = "day";
        this.monthFlag = true;
        this.weekFlag = false;
        this.dayFlag = false;
      }
      // 2592000000
      else if (timeStamp <= 2592000000) {
        // 三个月
        this.timeType = "day";
        this.monthFlag = false;
        this.weekFlag = false;
        this.dayFlag = false;
      } else if (timeStamp <= 15552000000) {
        // 六个月
        this.timeType = "week";
        this.monthFlag = false;
        this.weekFlag = false;
        this.dayFlag = false;
      } else if (timeStamp >= 31536000000) {
        this.timeType = "month";
        this.monthFlag = false;
        this.weekFlag = false;
        this.dayFlag = true;
      }
    },

    checkTimeSlot(val) {
      switch (val) {
        case true:
          this.checkLabel = "对比";
          this.contrastValue = "";
          break;
        case false:
          this.checkLabel = "对比时间段";
          this.contrastValue = "";
          this.dateFlag = null;
          break;
      }
      this.contrastFlag = val;
    },
  },
  beforeDestroy() {
    this.$bus.$off("$isOpen");
  },
};
</script>

<style lang="scss" scoped>
@import "~@/styles/components/TopFilter.scss";
::v-deep {
  .el-radio-button--mini .el-radio-button__inner {
    padding: 9px 15px;
  }
  .el-checkbox__input.is-checked + .el-checkbox__label {
    color: #4d4d4d;
  }
  .el-checkbox__label {
    font-size: 13px;
  }
  @import "~@/styles/components/custom-radio.scss";

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

::v-deep {
  .custom_input {
    .el-input__inner {
      font-size: 11px;
    }
  }

  .custom_input .el-input--medium .el-input__inner {
    width: 250px !important;
  }
  .el-button--medium {
    padding: 0 20px;
  }
  .el-input--medium .el-input__inner {
    height: 30px;
    line-height: 30px;
  }

  .el-input--medium .el-input__icon {
    line-height: 30px;
  }
  .el-input__inner {
    font-size: 13px;
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
}
.custom_button {
  height: 30px;
  color: #fff;
  font-size: 13px;
  background-color: #2c7be5;
}
.headerFilter {
  min-height: 60px;
  color: #194580;
  font-size: 13px;
  background: #fff;
  box-sizing: border-box;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: nowrap !important;
  overflow-x: auto;
  .tabBar {
    min-height: 60px;
    position: fixed;
    z-index: 999;
    background: #fff;
    display: flex;
    flex-direction: column;
    flex-wrap: nowrap !important;
    justify-content: center;
    overflow-x: auto;
    overflow-y: hidden;
    padding: 0 20px;
    width: 100%;
  }

  .check_item {
    background-color: #ffffff;
    margin-left: 20px;
    height: 30px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    span {
      font-size: 13px;
      font-weight: 400;
      line-height: 13px;
      color: #4d4d4d;
      padding: 0 10px;
    }
    .checkBoxStyle {
      padding-right: 10px;
    }
  }
  .singleItem {
    display: flex;
    margin-right: 30px;
  }
  .setTitle {
    height: 30px;
    line-height: 30px;
  }
  .custom_select {
    width: 100px;
    margin-left: 10px;
    height: 30px;
    line-height: 30px;
  }
  .warry_select {
    display: flex;
    border: 1px solid #acb2ba;
    border-radius: 4px;
    align-items: center;
    height: 30px;
    margin-right: 20px;
    .title {
      font-size: 12px;
      color: #4d4d4d;
      padding-left: 6px;
      white-space: nowrap;
    }
  }
}
</style>