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
              @change="checkDateEvent"
            >
            </el-date-picker>
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
              <div class="tabBarBtn" style="position: relative;right: 0;">
                <el-button class="zc_btn refresh" icon="el-icon-refresh-right" @click="refreshAPI"
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

          <!-- 第二行 -->
          <div style="display: flex; margin-left: 20px; margin-top: 12px">
            <div v-if="ByDayOrHour">
              <el-radio-group
                size="mini"
                v-model="timeType"
                style="font-size: 13px; height: 30px"
              >
                <el-radio-button label="hour" :disabled="dayFlag ? true : false"
                  >按时</el-radio-button
                >
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
            <div class="warry_select" v-if="ByGrop">
              <div class="title">分组:</div>
              <el-select
                v-model="groupName"
                class="single_select"
                placeholder="请选择状态"
              >
                <el-option
                  v-for="item in groupList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </div>
            <el-input
              v-if="BySearchWord"
              class="custom_input"
              style="width: 250px; border-radius: 4px; height: 30px"
              placeholder="请输入事件英文名"
              v-model="name"
              @keyup.enter.native="searchEvent()"
              clearable
            >
              <i
                @click="searchEvent"
                slot="prefix"
                class="el-input__icon el-icon-search"
              ></i>
            </el-input>
           
            <el-cascader
              :append-to-body="false"
              v-if="BySearchCustom"
              :key="generateRandomKey()"
              v-model="name"
              :options="protoList"
              :show-all-levels="false"
              style="font-size: 13px"
              :ref="'cascader_one'"
              class="custom_cascader"
              filterable
              :props="{
                value: 'name',
                label: 'displayName',
                children: 'events',
                multiple: false,
                emitPath: false,
              }"
              @change="fileterCascader"
            ></el-cascader>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import { copyObj } from "@/utils/copy";
import {
  getGroupNameListEventApi,
  getGroupedPropertiesApi,
} from "@/api/sysmanage/manageEvent";
import { getListNoGroupEventApi } from "@/api/sysmanage/manageEvent";
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
    BySearchCustom: {
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
      monthFlag: false,
      weekFlag: false,
      dayFlag: false,
      timeType: "hour",
      groupList: [],
      all: {
        label: "全部",
        value: "",
      },
      // start
      protoList: [],
      // ----
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
      // searchWord: "",
      startTime: "",
      endTime: "",
      name: "", //搜索词
    };
  },
  mounted() {
    this.initDateHandle();
    this.getPageList(); //事件分组
    // this.getPrototype();
  },
  computed: {
    projectName() {
      return this.$store.getters.projectName;
    },
    ...mapState({
      device: (state) => (state.app.device ? state.app.device : "desktop"),
    }),
    defaultParams() {
      const { startTime, endTime } = this;
      return {
        startTime,
        endTime,
      };
    },
    commonParams() {
      const { name, groupName } = this;
      let obj = {};
      obj = Object.assign(obj, this.defaultParams);
      if (this.BySearchWord) {
        // this.name = this.searchWord
        obj = Object.assign(obj, { name });
      }
      if (this.BySearchCustom) {
        obj = Object.assign(obj, { name });
      }
      if (this.ByGrop) {
        obj = Object.assign(obj, { groupName });
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
            this.setTopFilterParams(newVal);
          });
        }
      },
    },
  },
  methods: {
    fileterCascader(event) {
      // this.name = this.searchWord;
      // this.refreshAPI();
    },
    generateRandomKey() {
      let randomNumber = "";
      randomNumber = this.$options.filters.createUniqueString();
      return randomNumber;
    },
    getPrototype() {
      getGroupedPropertiesApi({
        projectName: this.projectName,
      }).then((res) => {
        if (res.code == 200) {
          if (res.data) {
            res.data.map((item, index) => {
              item.displayName = item.groupName;
              item.name = item.groupName;
              item.events.map((sub, sub_index) => {
                if (sub_index == 0 && index == 0) {
                  this.eventType = sub.name;
                }
              });
            });
            this.protoList = res.data;
            let all = {
              displayName: "全部",
              name: " ",
            };
            this.protoList.unshift(all);
          }
        }
      });
    },

    refreshAPI() {
      let params = copyObj(this.commonParams);
      // params.name = this.searchWord;
      this.setTopFilterParams(params);
    },
    collapseResetApiList() {
      this.timeFlag = 'day';
      this.handleChange(this.timeFlag)
      // this.searchWord = ''
      this.name = '';
      this.groupName = ''
    },
    getPageList() {
      let params = {
        projectName: this.projectName,
      };
      if (this.ByGrop) {
        getGroupNameListEventApi(params).then((res) => {
          if (res.code == 200) {
            this.groupList = res.data.filter((item) => {
              if (typeof item === "string") {
                return item.trim() !== "";
              }
              return true;
            });
            this.groupList = this.groupList.map((item) => {
              return { ["label"]: item, ["value"]: item };
            });
            this.groupList.unshift(this.all);
          }
        });
      }
    },
    searchEvent() {
      // this.name = this.searchWord;
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
    checkDateEvent(val) {
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
<style>
.el-select-dropdown__empty {
  display: none !important;
}
</style>
<style lang="scss" scoped>
@import "~@/styles/components/TopFilter.scss";
::v-deep {
  .el-select .el-input .el-select__caret {
    line-height: 30px;
  }
  .el-select-dropdown__list {
    padding: 0 0 6px;
  }
  .el-radio-group {
    margin: 0px;
  }
  .el-radio-button {
    height: 20px;
    font-size: 12px;
  }
  .el-radio-button--medium .el-radio-button__inner {
    padding: 6px;
    font-size: 13px;
  }
  .el-input--medium .el-input__inner {
    height: 30px;
    line-height: 30px;
    font-size: 12px;
  }

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
.empty-text {
  display: flex;
  text-align: center;
  color: #909399;
  padding: 10px 0;
  margin-bottom: 10px;
}
.select_empty {
  text-align: center;
  margin-bottom: 10px;
  font-size: 12px;
  color: #909399;
}
.custom_button {
  height: 30px;
  color: #fff;
  font-size: 13px;
  background-color: #2c7be5;
}
.headerFilter {
  min-height: 105px;
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
    min-height: 105px;
    position: fixed;
    z-index: 999;
    background: #fff;
    display: flex;
    flex-direction: column;
    flex-wrap: nowrap !important;
    justify-content: center;
    overflow-x: auto;
    overflow-y: hidden;
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
