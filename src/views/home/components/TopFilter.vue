<template>
    <div class="TopFilter">
      <div class="radio-com">
        <span class="radio-com-label">时间：</span>
        <el-radio-group v-model="timeType" class="radio-com-el">
          <el-radio label="day">今日</el-radio>
          <el-radio label="week">本周</el-radio>
          <el-radio label="month" >本月</el-radio>
          <!-- <el-radio label="year">本年</el-radio> -->
        </el-radio-group>
      </div>

      <div class="radio-com">
        <span class="radio-com-label">渠道：</span>
        <el-radio-group class="radio-com-el" v-model="channelValue">
          <el-radio label="">全部</el-radio>
          <el-radio label="安卓">安卓</el-radio>
          <el-radio label="苹果">苹果</el-radio>
          <el-radio label="网站">网站</el-radio>
          <el-radio label="微信小程序">微信小程序</el-radio>
        </el-radio-group>
      </div>
    </div>
</template>

<script>
import { timestampToTime } from "@/utils/timestampToTime";
import { formatDate } from "@/utils/format";
export default {
  name: "TopFilter",
  data() {
    return {
      timeType: "day",
      channelValue: "",
      weekEnd: "",
      weekStart: "",
    };
  },
  computed: {
    channel() {
      return [this.channelValue];
    },
    startTime() {
      switch (this.timeType) {
        case "day":
          return formatDate(new Date());
          break;
        case "week":
          // let toData =
          //   new Date(new Date().toLocaleDateString()).getTime() +
          //   8 * 3600 * 1000;
          // let timeDifference = toData - 6 * 3600 * 24 * 1000;
          // return timestampToTime(timeDifference);
          this.getWeek();
          return this.weekStart;
          break;
        case "month":
          let monthStart = new Date();
          monthStart.setDate(1);
          monthStart = this.formData(monthStart);
          return monthStart;
          break;
        case "year":
          let today = formatDate(new Date());
          let yearData = this.getToYear(today.slice(0, 4)).split("/");
          return yearData[0];
          break;
        default:
          return formatDate(new Date());
          break;
      }
    },
    endTime() {
      switch (this.timeType) {
        case "day":
          return formatDate(new Date());
          break;
        case "week":
          // return formatDate(new Date());
          this.getWeek();
          return this.weekEnd;
          break;
        case "month":
          let monthStart = new Date();
          monthStart.setDate(1);
          monthStart = this.formData(monthStart);
          let monthEnd = new Date(monthStart);
          monthEnd.setMonth(monthEnd.getMonth() + 1);
          monthEnd.setDate(0);
          monthEnd = this.formData(monthEnd);
          return monthEnd;
          break;
        case "year":
          let today = formatDate(new Date());
          let yearData = this.getToYear(today.slice(0, 4)).split("/");
          return yearData[1];
          break;

        default:
          return formatDate(new Date());
          break;
      }
    },
    implied() {
      const { startTime, endTime } = this;
      return { startTime, endTime };
    },
    filterParams() {
      const { timeType, channel } = this;
      return { timeType, channel };
    },
  },
  mounted() {
    this.setTopFilterParams(this.filterParams);
  },
  watch: {
    filterParams(val) {
      return this.setTopFilterParams(val);
    },
  },
  methods: {
    // 获取week 的函数
    getWeek() {
      let date = new Date();
      let day = date.getDate();
      let week = date.getDay();
      let month = date.getMonth();
      let year = date.getFullYear();
      this.weekStart = this.formData(new Date(year, month, day - week + 1));
      // this.weekEnd = this.formData(new Date(year, month, day - week + 7));
      this.weekEnd = this.formData(new Date(year, month, day));
    },
    setTopFilterParams(val) {
      let _val = Object.assign(val, this.implied);
      this.$emit("setTopFilterParams", _val);
      this.$emit("timeTypeCheck", this.timeType);
    },
    formData(val) {
      return (
        val.getFullYear() +
        "-" +
        (val.getMonth() + 1 < 10
          ? "0" + (val.getMonth() + 1)
          : val.getMonth() + 1) +
        "-" +
        (val.getDate() < 10 ? "0" + val.getDate() : val.getDate())
      );
    },
    getToYear(vars) {
      return vars + "-01-01/" + vars + "-12-31";
    },
  },
};
</script>

<style lang="scss" scoped>
@import "~@/styles/components/TopFilter.scss";
::v-deep {
  @import "~@/styles/components/custom-radio.scss";
}
</style>
