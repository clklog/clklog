<template>
  <div class="TopFilter">
    <div class="radio-com">
      <span class="radio-com-label">时间：</span>
      <el-radio-group
        v-model="timeType"
        class="radio-com-el"
        @change="handleChange"
      >
        <el-radio label="day">今日</el-radio>
        <el-radio label="week">本周</el-radio>
        <el-radio label="month">本月</el-radio>
        <el-radio label="year">本年</el-radio>
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
export default {
  name: "TopFilter",
  data() {
    return {
      timeType: "day",
      channelValue: "",
      // startTime: "2023-06-07",
      // endTime: "2023-06-08",
      startTime:"",
      endTime:"",
    };
  },
  computed: {
    channel() {
      return [this.channelValue];
    },
    filterParams() {
      const { timeType, channel, startTime, endTime } = this;
      return { timeType, channel, startTime, endTime };
    },
  },
  mounted() {
    this.startTime = new Date().getFullYear() +
        "-" +
        (new Date().getMonth() + 1 < 10
          ? "0" + (new Date().getMonth() + 1)
          : new Date().getMonth() + 1) +
        "-" +
        new Date().getDate()
    this.endTime = new Date().getFullYear() +
        "-" +
        (new Date().getMonth() + 1 < 10
          ? "0" + (new Date().getMonth() + 1)
          : new Date().getMonth() + 1) +
        "-" +
        new Date().getDate()
    // this.setTopFilterParams(this.filterParams);
  },
  watch: {
    filterParams(val) {
      return this.setTopFilterParams(val);
    },
  },
  methods: {
    setTopFilterParams(val) {
      this.$emit("setTopFilterParams", val);
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
    handleChange(val) {
      let date = new Date();
      let toData = new Date(new Date().toLocaleDateString()).getTime() + 8 * 3600 * 1000;
      let today =
        new Date().getFullYear() +
        "-" +
        (new Date().getMonth() + 1 < 10
          ? "0" + (new Date().getMonth() + 1)
          : new Date().getMonth() + 1) +
        "-" +
        new Date().getDate();

      if (val == "day") {
        this.startTime = today;
        this.endTime = today;
      } else if (val == "month") {
        let monthStart = new Date();
        monthStart.setDate(1);
        monthStart = this.formData(monthStart);
        let monthEnd = new Date(monthStart);
        monthEnd.setMonth(monthEnd.getMonth() + 1);
        monthEnd.setDate(0);
        monthEnd = this.formData(monthEnd);

        this.startTime = monthStart;
        this.endTime = monthEnd;
      } else if (val == "week") {
        let timeDifference = toData - 6 * 3600 * 24 * 1000;
        this.startTime = timestampToTime(timeDifference);
        this.endTime = today;
      } else {
        // timestampToTime(timeDifference);
      }
    },
  },
};
</script>

<style lang="scss" scoped>
@import "~@/styles/components/TopFilter.scss";
</style>
