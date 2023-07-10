<template>
  <div class="documentation-container">
    <div class="checkContent">
      <div style="display: flex; margin-top: 11px">
        <div class="check_item">
          <span>时间:</span>
          <el-radio-group
            v-model="dateList"
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

        <div style="margin-left: 20px; height: 30px">
          <el-radio-group
            size="mini"
            v-model="specificTTime"
            style="font-size: 12px; height: 30px"
          >
            <!-- :disabled="is_commite == 1 ? true:false" -->
            <el-radio-button label="time" :disabled="dayFlag ? true : false"
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

        <div v-if="areaFlag == 1" class="areaContent">
          <div class="areaItem">
            <div class="areaHead">地域</div>
            <!-- <div class="blocks">
              <el-cascader
                v-model="areaList"
                class="cascader2"
                style="width: 150px"
                :options="provinceData"
                :props="{ value: 'provinceName', label: 'provinceName', multiple: true, checkStrictly: true }"
                placeholder="选择"
                clearable
                collapse-tags
              />
            </div> -->
            <!-- 气泡弹框 -->
            <div>
              <el-popover placement="bottom" width="510" trigger="click">
                <div>
                  <el-checkbox-group v-model="areaList">
                    <el-checkbox
                      @change="handleCheckProvince(item)"
                      v-for="(item, index) in provinceData"
                      :key="index"
                      :label="item.provinceName"
                      >{{ item.provinceName }}</el-checkbox
                    >
                  </el-checkbox-group>
                </div>
                <div class="areaBox" slot="reference">
                  {{ areadefault }}
                  <div
                    v-if="areadefault && areadefault != '选择'"
                    style="
                      color: #4d4d4d;
                      margin-left: 5px;
                      border-radius: 50%;
                      height: 13px;
                      width: 13px;
                      border: 1px solid #4d4d4d;
                      display: flex;
                      align-items: center;
                      justify-content: center;
                      padding: 2px;
                    "
                  >
                    x
                  </div>
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
            <el-radio-group v-model="channelList" style="margin-right: 10px">
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
            <el-radio-group v-model="visitorValue" class="checkBoxStyle">
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
    areaFlag: {
      type: string,
      default: 1,
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
      areadefault: "选择",
      dateRange: null, //日期范围
      specificTTime: "time",
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
      // allChaneList: ["网站", "安卓", "苹果"],
      // areaList: this.$store.state.filterBar.checkList.areaList,
      areaList: [],
      dateList: this.$store.state.filterBar.checkList.dateList,
      visitorValue: this.$store.state.filterBar.checkList.visitorValue,
      dayFlag: true,
      weekFlag: true,
      monthFlag: true,
      checkDateTime: "",
      past7daysStart: "", //最近一周
      timeDifference: "",
    };
  },
  mounted() {
    this.handleAdd();
  },
  computed: {
    project() {
      return this.$store.getters.project;
    },
    provinceData() {
      return province;
    },
    // checkList() {
    //   return this.$store.state.filterBar.checkList;
    // },
    changeCheck() {
      const {
        dateList,
        channelList,
        project,
        visitorValue,
        areaList,
        dateRange,
      } = this;
      return {
        dateList,
        channelList,
        project,
        visitorValue,
        areaList,
        dateRange,
      };
    },
  },
  watch: {
    changeCheck(val) {
      const {
        dateList,
        channelList,
        project,
        visitorValue,
        areaList,
        dateRange,
      } = this;
      let channelCheck = "";
      if (!this.channelList) {
        channelCheck = [];
      } else {
        channelCheck = [this.channelList];
      }
      let checkDataList = {
        dateRange,
        dateList,
        channelCheck,
        project,
        visitorValue,
        areaList,
      };
      if (dateList != "year") {
        this.$emit("getFilterBar", checkDataList); // 切换头部
      } else if (this.dateRange) {
        this.$emit("getFilterBar", checkDataList);
      }
    },
  },
  methods: {
    // 切换省份
    handleCheckProvince(e) {
      // console.log(e);
      console.log(this.areaList, "选中的值");
      let areaList = this.areaList.join("、");
      // console.log(areaList);
      this.areadefault = areaList;
    },
    checkDateEvnet(val) {
      this.endTime = new Date(val[1]).getTime();
      this.startTime = new Date(val[0]).getTime();
      let toDay =
        new Date(new Date().toLocaleDateString()).getTime() + 8 * 3600 * 1000;
      let timeStamp = this.endTime - this.startTime;
      if (timeStamp == 518400000) {
        this.dateList = "week";
      } else if (timeStamp == 2505600000) {
        this.dateList = "month";
      } else if (timeStamp == 86400000) {
        this.dateList = "previous";
      } else if (
        !timeStamp &&
        this.startTime == toDay &&
        this.endTime == toDay
      ) {
        this.dateList = "day";
      } else {
        this.dateList = "";
      }
      let result = (this.endTime - this.startTime) / (3600 * 24 * 1000);
      this.dateTimeCount(result);
    },
    // 日期计算
    dateTimeCount(result) {
      if (6 <= result < 29) {
        this.weekFlag = false;
        this.dayFlag = false;
        this.monthFlag = true;
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
      }
    },
    // 日期初始值
    initDate(val) {
      this.startTime = val[0].split("-").join("");
      this.endTime = val[1].split("-").join("");
      let result = this.endTime - this.startTime;
      this.dateTimeCount(result);
    },
    // 默认初始日期
    handleAdd() {
      var date = new Date();
      let dateTime =
        date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
      this.currentTime = [dateTime, dateTime];
      this.initDate(this.currentTime); //默认日期
    },

    // 单选切换时间
    handleChange(val) {
      var date = new Date();
      let dateTime =
        date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
      let toData =
        new Date(new Date().toLocaleDateString()).getTime() + 8 * 3600 * 1000;
      if (val == "day") {
        this.currentTime = [dateTime, dateTime];
        this.dateTimeCount(1);
      } else if (val == "previous") {
        this.timeDifference = toData - 3600 * 24 * 1000;
        this.timestampToTime(this.timeDifference);
        this.currentTime = [this.checkDateTime, dateTime];
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

      // this.checkDateEvnet(this.currentTime)
      // if (val == "year") {
      //   if (this.$refs.tiemPick) this.$refs.tiemPick.focus();
      // }
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
        (date.getDate() < 10 ? "0" + date.getDate() : date.getDate()) + " ";
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
  .el-range-editor--medium .el-range-separator {
    line-height: 22px;
    font-size: 13px;
  }
  .el-cascader .el-input input {
    height: 30px !important;
  }
  .el-radio-button--mini .el-radio-button__inner {
    height: 30px;
  }
  .el-cascader--medium {
    line-height: 29px;
  }
  .el-range-editor--medium .el-range__icon,
  .el-range-editor--medium .el-range__close-icon {
    display: none;
  }
  .el-checkbox {
    margin-top: 11px;
  }
}

::v-deep .el-radio-button {
  line-height: 30px;
  height: 30px !important;
}
::v-deep.blocks {
  .el-input__inner {
    border-radius: 0 5px 5px 0 !important;
    border-left: #eee;
  }
  .el-cascader .el-input.is-focus .el-input__inner {
    border-color: #eee !important;
  }
  .el-cascader .el-input .el-input__inner:focus {
    border-color: #eee !important;
  }
}
.documentation-container {
  .checkContent {
    min-height: 60px;
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
          font-size: 11px;
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
          font-size: 11px;
          font-weight: 400;
          color: #4d4d4d;
          border: 1px solid #eee;
          border-top-right-radius: 5px;
          border-bottom-right-radius: 5px;
          padding: 0 8px;
          cursor: pointer;
          display: flex;
          align-items: center;
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
        font-size: 11px;
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
