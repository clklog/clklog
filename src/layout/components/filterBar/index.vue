<template>
  <div class="documentation-container">
    <div class="checkContent">
      <div style="display: flex; margin-top: 11px">
        <div class="check_item">
          <span>时间:</span>
          <el-radio-group
            v-model="timeFlag"
            class="checkBoxStyle"
            @change="handleChange"
          >
            <el-radio label="day">今日</el-radio>
            <el-radio label="previous">昨日</el-radio>
            <el-radio label="week">近7天</el-radio>
            <el-radio label="month">近30天</el-radio>
            <!-- <el-radio label="year"> 日期范围 </el-radio> -->
          </el-radio-group>
        </div>

        <div v-if="ByData" style="margin-left: 20px; height: 30px">
          <el-radio-group
            size="mini"
            v-model="timeType"
            style="font-size: 12px; height: 30px"
          >
            <el-radio-button label="hour" :disabled="dayFlag ? true : false"
              >按时</el-radio-button
            >
            <el-radio-button label="day" :disabled="dayFlag ? true : false"
              >按日</el-radio-button
            >
            <el-radio-button label="week" :disabled="weekFlag ? true : false"
              >按周</el-radio-button
            >
            <el-radio-button label="month" :disabled="monthFlag ? true : false"
              >按月</el-radio-button
            >
          </el-radio-group>
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

        <div v-if="ByArea" class="areaContent">
          <div class="areaItem">
            <div class="areaHead">地域</div>
            <!-- 气泡弹框 -->
            <div>
              <el-popover placement="bottom" width="510" trigger="click">
                <div>
                  <el-radio-group size="mini" v-model="areaValue">
                    <el-radio
                      style="margin-top: 11px"
                      @change="handleCheckProvince(item)"
                      v-for="(item, index) in provinceData"
                      :key="index"
                      :label="item.provinceName"
                      >{{ item.provinceName }}</el-radio
                    >
                  </el-radio-group>
                </div>
                <div class="areaBox" slot="reference">
                  {{ areaValue }}
                </div>
              </el-popover>
            </div>
          </div>
        </div>
      </div>

      <div class="channel">
        <div style="display: flex">
          <div class="check_item">
            <span>渠道:</span>
            <el-radio-group v-model="channelValue" style="margin-right: 10px">
              <el-radio
                v-for="item in allChaneList"
                :label="item.id"
                :key="item.id"
                >{{ item.label }}</el-radio
              >
            </el-radio-group>
          </div>
          <div class="check_item">
            <span>访客:</span>
            <el-radio-group v-model="visitorType" class="checkBoxStyle">
              <el-radio label="">全部</el-radio>
              <el-radio label="新访客">新访客</el-radio>
              <el-radio label="老访客">老访客</el-radio>
            </el-radio-group>
          </div>
        </div>
        <div style="display: flex; margin-right: 16px">
          <div class="btnEvent SetSpace">
            <i
              class="el-icon-message"
              style="padding-right: 3px; font-size: 14px"
            ></i
            >订阅
          </div>
          <div class="btnEvent">
            <i
              class="el-icon-download"
              style="padding-right: 3px; font-size: 14px"
            ></i
            >下载
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { string } from "clipboard";
import { province } from "@/utils/province";
export default {
  props: {
    ByData: {
      type: Boolean,
      default: false,
    },
    ByArea: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      pickerBeginOption: {
        disabledDate(date) {
          const today = new Date();
          // today.setHours(0, 0, 0, 0);
          return date.getTime() > today.getTime();
        },
      },
      dateRange: null, //日期范围
      isIndeterminate: false,
      checkAll: false,
      channelList: "",
      currentTime: "",
      allChaneList: [
        { label: "全部", id: "" },
        { label: "安卓", id: "安卓" },
        { label: "苹果", id: "苹果" },
        { label: "网站", id: "网站" },
        { label: "微信小程序", id: "微信小程序" },
      ],
      dateList: "day",
      visitorValue: "新访客",
      dayFlag: true,
      weekFlag: true,
      monthFlag: true,
      checkDateTime: "",
      past7daysStart: "", //最近一周
      timeDifference: "",

      // 接口参数数据
      timeType: this.ByData ? "hour" : "day",
      timeFlag: "day",
      startTime: "",
      endTime: "",
      areaValue: "北京市",
      channelValue: "", //渠道
      visitorType: "",
    };
  },
  mounted() {
    this.handleAdd();
  },
  computed: {
    channel() {
      return [this.channelValue];
    },
    area() {
      return [this.areaValue];
    },
    provinceData() {
      return province;
    },
    // 默认参数
    defaultParams() {
      const { startTime, endTime, channel, visitorType } = this;
      return {
        startTime,
        endTime,
        channel,
        visitorType,
      };
    },
    commonParams() {
      let obj = {};
      obj = Object.assign(obj, this.defaultParams);
      const { area, timeType } = this;
      if (this.ByArea) {
        obj = Object.assign(obj, { area });
      }
      if (this.ByData) {
        obj = Object.assign(obj, { timeType });
      }
      return obj;
    },
  },
  watch: {
    commonParams(val) {
      return this.setTopFilterParams(val);
    },
  },
  methods: {
    setTopFilterParams(val) {
      this.$emit("setFilterBarParams", val);
    },
    // 切换省份
    handleCheckProvince(e) {
      this.areaValue = e.provinceName;
    },
    checkDateEvnet(val) {
      this.startTime = val[0];
      this.endTime = val[1];
      let endTime = new Date(val[1]).getTime();
      let startTime = new Date(val[0]).getTime();
      let toDay =
        new Date(new Date().toLocaleDateString()).getTime() + 8 * 3600 * 1000;
      let timeStamp = endTime - startTime;
      if (timeStamp == 518400000) {
        this.dateList = "week";
      } else if (timeStamp == 2505600000) {
        this.dateList = "month";
      } else if (timeStamp == 86400000) {
        this.dateList = "previous";
      } else if (!timeStamp && startTime == toDay && endTime == toDay) {
        this.dateList = "day";
      } else {
        this.dateList = "";
      }
      let result = (endTime - startTime) / (3600 * 24 * 1000);
      this.dateTimeCount(result);
    },
    // 日期计算
    dateTimeCount(result) {
      if (6 <= result < 29) {
        this.weekFlag = false;
        this.dayFlag = false;
        this.monthFlag = true;
        switch (this.timeType) {
          case "month":
            this.timeType = "week";
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

          default:
            break;
        }
      }
    },
    // 日期初始值
    initDate(val) {
      let startTime = val[0].split("-").join("");
      let endTime = val[1].split("-").join("");
      this.startTime = val[0];
      this.endTime = val[1];
      let result = endTime - startTime;
      this.dateTimeCount(result);
    },
    // 默认初始日期
    handleAdd() {
      var date = new Date();
      let dateTime =
        date.getFullYear() +
        "-" +
        (date.getMonth() + 1 < 10
          ? "0" + (date.getMonth() + 1)
          : date.getMonth() + 1) +
        "-" +
        date.getDate();
      this.currentTime = [dateTime, dateTime];
      this.initDate(this.currentTime); //默认日期
    },

    // 单选切换时间
    handleChange(val) {
      var date = new Date();
      let dateTime =
        date.getFullYear() +
        "-" +
        (date.getMonth() + 1 < 10
          ? "0" + (date.getMonth() + 1)
          : date.getMonth() + 1) +
        "-" +
        date.getDate();
      let toData =
        new Date(new Date().toLocaleDateString()).getTime() + 8 * 3600 * 1000;
      if (val == "day") {
        this.currentTime = [dateTime, dateTime];
        this.dateTimeCount(1);
      } else if (val == "previous") {
        this.timeDifference = toData - 3600 * 24 * 1000;
        this.timestampToTime(this.timeDifference);
        this.currentTime = [this.checkDateTime, this.checkDateTime];
        this.dateTimeCount(1);
      } else if (val == "week") {
        this.timeDifference = toData - 6 * 3600 * 24 * 1000;
        this.timestampToTime(this.timeDifference);
        this.currentTime = [this.checkDateTime, dateTime];
        this.dateTimeCount(6);
      } else {
        this.timeDifference = toData - 29 * 3600 * 24 * 1000;
        this.timestampToTime(this.timeDifference);
        this.currentTime = [this.checkDateTime, dateTime];
        this.dateTimeCount(29);
      }
      this.startTime = this.currentTime[0];
      this.endTime = this.currentTime[1];
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

    handleCheckAllChange(val) {
      this.channelList = val ? this.allChaneList : [];
      this.isIndeterminate = false;
    },
    handleCheckedCitiesChange(value) {
      const checkedCount = value.length;
      if (checkedCount === this.allChaneList.length) {
        this.checkAll = false;
      } else {
        this.checkAll = false;
      }
      this.isIndeterminate =
        checkedCount > 0 && checkedCount < this.channelList.length;
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep {
  @import "~@/styles/components/custom-radio.scss";
  // 日历样式start
  .el-range-editor--medium .el-range-input {
    font-size: 12px;
  }
  .el-date-editor .el-range__icon {
    font-size: 12px;
    line-height: 25px;
  }
  .el-range-editor--medium .el-range-separator {
    font-size: 12px;
    line-height: 25px;
  }
}
.documentation-container {
  height: 83px;
  width: 100%;
  .checkContent {
    position: fixed;
    z-index: 500;
    background-color: #fff;
    min-height: 60px;
    width: 100%;
    border-bottom: 1px #eee solid;
    .areaContent {
      height: 30px;
      margin-left: 15px;
      .areaItem {
        display: flex;
        align-items: center;
        .areaHead {
          width: 40px;
          height: 30px;
          line-height: 30px;
          text-align: center;
          font-size: 12px;
          font-weight: 400;
          color: #4d4d4d;
          border: 1px solid #eee;
          border-top-left-radius: 5px;
          border-bottom-left-radius: 5px;
        }
        .areaBox {
          min-width: 78px;
          height: 30px;
          line-height: 30px;
          font-size: 12px;
          font-weight: 400;
          color: #4d4d4d;
          border: 1px solid #eee;
          border-top-right-radius: 5px;
          border-bottom-right-radius: 5px;
          padding: 0 8px;
          cursor: pointer;
        }
      }
    }
    .check_item {
      background-color: #ffffff;
      margin-left: 15px;
      height: 30px;
      border-radius: 4px;
      border: 1px solid #eee;
      display: flex;
      align-items: center;
      // width: 30%;
      span {
        font-size: 12px;
        font-weight: 400;
        line-height: 13px;
        color: #4d4d4d;
        padding: 0 10px;
      }
      .checkBoxStyle {
        padding-right: 10px;
      }
    }
    .channel {
      display: flex;
      margin: 11px 0;
      justify-content: space-between;
      align-items: center;
      .btnEvent {
        cursor: pointer;
        width: 54px;
        height: 21px;
        border: 1px solid #3d64e6;
        border-radius: 4px;
        font-size: 12px;
        font-weight: 400;
        line-height: 21px;
        text-align: center;
        color: #656d92;
      }
      .SetSpace {
        margin-right: 9px;
      }
    }
  }
}
</style>
