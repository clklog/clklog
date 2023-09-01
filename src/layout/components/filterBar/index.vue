<template>
  <div class="documentation-container">
    <div class="checkContent">
      <div style="display: flex; padding-top: 15px">
        <div class="check_item public_border_color">
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

        <div v-if="ByData" style="margin-left: 20px; height: 30px">
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
            <el-radio-button label="week" :disabled="weekFlag ? true : false"
              >按周</el-radio-button
            >
            <el-radio-button label="month" :disabled="monthFlag ? true : false"
              >按月</el-radio-button
            >
          </el-radio-group>
        </div>

        <div v-if="ByArea" class="areaContent">
          <div class="areaItem">
            <div class="areaHead public_border_color">地域</div>
            <!-- 气泡弹框 -->
            <div class="area_select sing_special_popover">
              <el-popover
                placement="bottom"
                width="510"
                trigger="click"
                v-model="popflag"
              >
                <div style="position: relative">
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
                  <div class="popCancle" @click="canclePopEvent">X</div>
                </div>
                <div class="areaBox public_font_color" slot="reference">
                  {{ areaValue }}
                  <i v-if="popflag" class="el-icon-arrow-up iconArrow"></i>
                  <i v-else class="el-icon-arrow-down iconArrow"></i>
                </div>
              </el-popover>
            </div>
          </div>
        </div>
      </div>

      <div class="channelSecond">
        <div style="display: flex">
          <div class="check_item public_border_color" v-if="!byChnnel">
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
          <div class="check_item public_border_color">
            <span>访客:</span>
            <el-radio-group v-model="visitorType" class="checkBoxStyle">
              <el-radio label="">全部</el-radio>
              <el-radio label="新访客">新访客</el-radio>
              <el-radio label="老访客">老访客</el-radio>
            </el-radio-group>
          </div>
        </div>

        <div style="display: flex; position: fixed; right: 30px">
          <!-- <div class="btnEvent SetSpace">
            <i
              class="el-icon-message"
              style="padding-right: 3px; font-size: 14px"
            ></i
            >订阅
          </div> -->
          <div class="btnEvent" @click="download">
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
import { blobDownloads } from "@/utils/localDownloadUtil.js";
import { string } from "clipboard";
import { province } from "@/utils/province";
import {
  exportSearchWordDetailApi,
  exportVisitorApi,
  exportVisitorListApi,
} from "@/api/trackingapi/download";
export default {
  props: {
    bySub: {
      type: Boolean,
      default: false,
    },
    byChnnel: {
      type: Boolean,
      default: false,
    },
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
      showBtn: true,
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
      // timeType: this.ByData ? "day" : "day",
      timeFlag: "day",
      startTime: "",
      endTime: "",
      areaValue: "全部",
      channelValue: "", //渠道
      visitorType: "",
      popflag: false,
      commonData: "",
    };
  },
  mounted() {
    this.handleAdd();
  },
  computed: {
    channel() {
      if (this.channelValue) {
        return [this.channelValue];
      } else {
        return [];
      }
      // return [this.channelValue];
    },
    // area() {
    //   return [this.areaValue];
    // },
    province() {
      if (this.areaValue == "全部") {
        return [];
      } else {
        return [this.areaValue];
      }
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
      // const { area, timeType } = this;
      const { province, timeType } = this;
      if (this.ByArea) {
        // obj = Object.assign(obj, { area });

        obj = Object.assign(obj, { province });
      }
      if (this.ByData) {
        obj = Object.assign(obj, { timeType });
      }
      return obj;
    },
  },
  watch: {
    commonParams(val) {
      // return this.setTopFilterParams(val);
      this.setTopFilterParams(val);
      this.commonData = val;
    },
  },
  methods: {
    btnShowEvent(val) {
      // console.log(val,"点击事件");
      this.popflag = true;
    },
    canclePopEvent() {
      this.popflag = false;
    },
    download() {
      this.$bus.$emit("publicEventDown", this.commonData);
      let path = this.$route.path;
      this.commonData.project = this.$store.getters.project;
      switch (path) {
        case "/visitorAnalysis/searchAnalysis": {
          let cols = [
            "index",
            "searchword",
            "pv",
            "pvRate",
            "visitCount",
            "visitCountRate",
            "uv",
            "newUv",
            "ipCount",
            "ipCountRate",
            "avgVisitTime",
            "avgPv",
            "bounceRate",
          ];
          this.commonData.cols = cols;
          exportSearchWordDetailApi(this.commonData).then((res) => {
            let name = this.sliceTypeFile(res);
            blobDownloads(res.data, name);
          });
          break;
        }
        case "/visitorAnalysis/userLoyaltyAnalysis": {
          exportVisitorApi(this.commonData).then((res) => {
            let name = this.sliceTypeFile(res);
            blobDownloads(res.data, name);
          });
          break;
        }
        case "/behaviorAnalysis/user-behavior-analysis": {
          let cols = [
            "distinctId",
            "visitorType",
            "visitCount",
            "pv",
            "visitTime",
            "avgPv",
            "latestTime",
          ];
          this.commonData.cols = cols;
          exportVisitorListApi(this.commonData).then((res) => {
            let name = this.sliceTypeFile(res);
            blobDownloads(res.data, name);
          });
          break;
        }
      }
    },
    sliceTypeFile(res) {
      let fileName = res.headers["content-disposition"] || "";
      let index1 = fileName.indexOf("filename=");
      let result = fileName.substring(index1);
      result = decodeURIComponent(decodeURI(result.slice(9)));
      return result;
    },
    setTopFilterParams(val) {
      this.$emit("setFilterBarParams", val);
    },
    // 切换省份
    handleCheckProvince(e) {
      console.log(e, "全部");
      // if ((e.provinceName = "全部")) {
      //   this.areaValue = [];
      // }
      this.areaValue = e.provinceName;
      // switch (e.provinceName) {
      //   case "全部":
      //     this.areaValue = "全部";
      //     break;
      //   default:
      //     this.areaValue = e.provinceName;
      //     break;
      // }
    },
    checkDateEvnet(val) {
      this.timeFlag = "";
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
<style lang="scss">
// .sing_special_popover {
//   .el-popover {
//     background-color: red !important;
//   }
// }
</style>
<style lang="scss" scoped>
::v-deep {
  @import "~@/styles/components/custom-radio.scss";

  // 日历样式start
  .el-range-editor--medium .el-range-input {
    font-size: 13px;
  }
  .el-date-editor .el-range__icon {
    font-size: 13px;
    line-height: 25px;
  }
  .el-range-editor--medium .el-range-separator {
    font-size: 13px;
    line-height: 25px;
  }
}
.popCancle:hover {
  border: 1px solid #727171;
  border-radius: 4px;
}
.popCancle {
  position: absolute;
  right: 0;
  top: 0;
  cursor: pointer;
  width: 20px;
  height: 20px;
  border-radius: 4px;
  border: 1px #d8e2ef solid;
  line-height: 20px;
  text-align: center;
}
.documentation-container {
  box-sizing: border-box;
  min-height: 103px;
  padding-bottom: 20px;
  width: 100%;
  .checkContent {
    position: fixed;
    width: 100%;
    min-height: 103px;
    z-index: 500;
    background-color: #fff;
    border-bottom: 1px #eee solid;
    .areaContent {
      height: 30px;
      margin-left: 15px;
      .area_select {
        position: relative;
        .iconArrow {
          position: absolute;
          font-size: 13px;
          top: 3px;
          right: 5px;
          transform: translate(0, 50%);
        }
      }
      .areaItem {
        display: flex;
        align-items: center;
        .areaHead {
          width: 40px;
          height: 28px;
          line-height: 30px;
          text-align: center;
          font-size: 13px;
          font-weight: 400;
          color: #4d4d4d;
          border-top-left-radius: 5px;
          border-bottom-left-radius: 5px;
        }

        .areaBox {
          position: relative;
          height: 28px !important;
          line-height: 30px;
          font-size: 12px;
          font-weight: 400;
          border: 1px solid #d8e2ef;
          border-left: 0px;
          border-top-right-radius: 5px;
          border-bottom-right-radius: 5px;
          padding: 0 8px;
          padding-right: 20px;
          box-sizing: border-box;
          cursor: pointer;
        }
      }
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
    .btnEvent {
      cursor: pointer;
      width: 65px;
      height: 28px;
      color: #194580;
      font-weight: 600px;
      border: 1px solid #d8e2ef;
      border-radius: 4px;
      font-size: 12px;
      line-height: 28px;
      text-align: center;
    }
    .SetSpace {
      margin-right: 9px;
    }
    .channelSecond {
      width: 100%;
      display: flex;
      padding: 12px 0 15px 0;
      align-items: center;
      position: relative;
    }
  }
}
</style>
