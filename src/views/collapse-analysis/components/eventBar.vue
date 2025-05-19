<template>
  <div>
    <div class="documentation-container">
      <div class="headerFilter">
        <!-- 第一行 -->
        <div class="tabBar">
          <div style="display: flex; align-items: center; white-space: nowrap">
            <div class="check_item public_border_color" style="height: 30">
              <span>时间:</span>
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
            <div v-if="ByDayOrHour" style="margin-left: 20px">
              <el-radio-group
                size="mini"
                v-model="timeType"
                style="font-size: 13px; height: 30px"
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

            <div style="margin-left: 20px; line-height: 30px" v-if="ByContrast">
              <el-checkbox v-model="checked" @change="checkTimeSlot">{{
                checkLabel
              }}</el-checkbox>
            </div>

            <div
              :style="
                device == 'desktop'
                  ? 'position: fixed; right: 50px'
                  : 'position:relative;margin-left:50px;margin-right:30px'
              "
              v-if="ByRefresh"
            >
              <el-button class="custom_button" @click="refreshAPI"
                >查询</el-button
              >
            </div>
            <!-- 日期 -->
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
          </div>

          <!-- 第二行 -->
          <div style="display: flex; margin-left: 20px; margin-top: 12px">
            <!-- <div class="warry_select">
              <div class="title">应用版本:</div>
              <el-select
                style="width: 100px"
                v-model="version"
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
            <el-input
              class="custom_input"
              placeholder="请输入应用版本"
              style="width: 200px; border-radius: 4px"
              v-model="versionEqual"
              @keyup.enter.native="searchVersion()"
              clearable
              @blur="searchVersion"
              @clear="searchVersion"
            >
              <i
                @click="searchVersion"
                slot="prefix"
                class="el-input__icon el-icon-search"
              ></i>
            </el-input>

            <div class="warry_select">
              <div class="title">操作系统:</div>
              <el-select
                style="width: 100px"
                v-model="channel"
                class="single_select"
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
            <el-input
              class="custom_input"
              style="width: 250px; border-radius: 4px; height: 30px"
              placeholder="请输入设备类型"
              v-model="modelEqual"
              @keyup.enter.native="searchmodel()"
              @clear="searchmodel"
              @blur="searchmodel"
              clearable
            >
              <i
                @click="searchmodel"
                slot="prefix"
                class="el-input__icon el-icon-search"
              ></i>
            </el-input>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import { copyObj } from "@/utils/copy";
import { getGroupNameListEventApi } from "@/api/sysmanage/manageEvent";
export default {
  props: {
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
    ByDayOrHour: {
      type: Boolean,
      default: false,
    },
  },

  data() {
    return {
      optionList: [],
      allChaneList: [
        { label: "全部", id: "" },
        { label: "安卓", id: "安卓" },
        { label: "苹果", id: "苹果" },
      ],
      monthFlag: true,
      weekFlag: true,
      dayFlag: false,
      timeType: "day",
      groupList: [],
      version: "",
      channel: "",
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
      modelEqual: "",
      startTime: "",
      endTime: "",
      name: "", //搜索词
      eventName: "", //事件名称
      apiFlag_refresh: 1,
      versionEqual: "",
      version: "",
      modelEqual: "",
      model: "",
      commonData:'',
    };
  },
  mounted() {},
  computed: {
    projectName() {
      return this.$store.getters.projectName;
    },
    ...mapState({
      device: (state) => (state.app.device ? state.app.device : "desktop"),
    }),
    defaultParams() {
      const { startTime, endTime, version, channel, model } = this;
      return {
        startTime,
        endTime,
        version,
        channel,
        model,
      };
    },
    commonParams() {
      const { name, timeType } = this;
      let obj = {};
      obj = Object.assign(obj, this.defaultParams);
      if (this.BySearchWord) {
        obj = Object.assign(obj, { name });
      }

      if (this.ByDayOrHour) {
        obj = Object.assign(obj, { timeType });
      }
      return obj;
    },
  },
  watch: {
    commonParams: {
      deep: false,
      immediate: false,
      handler(newVal, oldVal) {
        if (JSON.stringify(newVal) !== JSON.stringify(oldVal)) {
          this.$nextTick(() => {
            this.commonData = newVal;
            if (this.apiFlag_refresh > 1) {
              this.setTopFilterParams(newVal);
            }

            this.apiFlag_refresh++;
          });
        }
      },
    },
  },
  methods: {
    searchVersion() {
      this.version = this.versionEqual;
    },
    searchmodel() {
      this.model = this.modelEqual;
    },
    statisticEvent(params, flag) {
      this.apiFlag_refresh = flag;
      const toDate = this.$options.filters.todateFunc(); //当日
      let timeStamp =
        new Date(params.endTime).getTime() -
        new Date(params.startTime).getTime();
      if (timeStamp == 0) {
        if (toDate == params.startTime) {
          this.timeFlag = "day";
        } else if (params.startTime == this.yesterdayFunc()) {
          this.timeFlag = "previous";
        } else {
          this.timeFlag = "";
        }
      } else if (timeStamp == 518400000) {
        this.timeFlag = "week";
      } else if (timeStamp == 2505600000) {
        this.timeFlag = "month";
      } else {
        this.timeFlag = "";
      }
      this.currentTime = [params.startTime, params.endTime];
      this.startTime = params.startTime;
      this.endTime = params.endTime;
      this.eventName = params.eventName;
      this.byCheckimeEvent(timeStamp);
      //  显页面带来的参数
      // if (params.channel || params.channel.length > 0) {
      //   if (params.channel == "Android") {
      //     this.channel = "安卓";
      //   } else {
      //     this.channel = "苹果";
      //   }
      // }
      this.channel = params.channel.toString();

      this.modelEqual = params.model;
      this.searchVersion();
      this.versionEqual = params.version;
      this.searchmodel();
    },
    yesterdayFunc() {
      let yesterday = new Date();
      yesterday.setDate(yesterday.getDate() - 1);

      let year = yesterday.getFullYear();
      let month =
        yesterday.getMonth() + 1 < 10
          ? "0" + (yesterday.getMonth() + 1)
          : yesterday.getMonth() + 1;
      let day =
        yesterday.getDate() < 10
          ? "0" + yesterday.getDate()
          : yesterday.getDate();

      return year + "-" + month + "-" + day;
    },
    byCheckimeEvent(timeStamp) {
      if (timeStamp == 0) {
        this.timeType = "day";
      } else if (timeStamp >= 518400000 && timeStamp < 2592000000) {
        this.timeType = "day";
      } else if (timeStamp >= 2592000000 && timeStamp < 7776000000) {
        this.timeType = "week";
      } else {
        this.timeType = "month";
      }
    },
    refreshAPI() {
      let params = copyObj(this.commonParams);
      params.name = this.modelEqual;
      this.setTopFilterParams(params);
    },
    getPageList() {
      let params = {
        projectName: this.projectName,
      };
    },
    searchEvent() {
      this.name = this.modelEqual;
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
      } else if (val == "previous") {
        this.timeDifference = toData - 3600 * 24 * 1000;
        this.timestampToTime(this.timeDifference);
        this.currentTime = [this.checkDateTime, this.checkDateTime];
      } else if (val == "week") {
        this.timeDifference = toData - 6 * 3600 * 24 * 1000;
        this.timestampToTime(this.timeDifference);
        this.currentTime = [this.checkDateTime, dateTime];
      } else {
        this.timeDifference = toData - 29 * 3600 * 24 * 1000;
        this.timestampToTime(this.timeDifference);
        this.currentTime = [this.checkDateTime, dateTime];
      }
      this.startTime = this.currentTime[0];
      this.endTime = this.currentTime[1];
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
  .tabBar {
    min-height: 105px;
    width: calc(80vw - 40px);
    border-radius: 6px;
    background: #fff;
    display: flex;
    flex-direction: column;
    flex-wrap: nowrap !important;
    justify-content: center;
    overflow-x: auto;
    overflow-y: hidden;
    box-sizing: border-box;
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
    border: 1px solid #d5dfed;
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
